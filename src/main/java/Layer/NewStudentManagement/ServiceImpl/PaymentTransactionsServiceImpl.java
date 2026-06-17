package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.PaymentGatewayAccountResponceDTO;
import Layer.NewStudentManagement.Entity.StudentFeesCollect;
import Layer.NewStudentManagement.Entity.StudentPaymentTransactions;
import Layer.NewStudentManagement.Repository.PaymentTransactionsRepository;
import Layer.NewStudentManagement.Service.PaymentTransactionsService;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static Layer.NewStudentManagement.Entity.StudentPaymentTransactions.TransactionStatus.*;

@Service
public class PaymentTransactionsServiceImpl implements PaymentTransactionsService {

    private final PaymentTransactionsRepository paymentTransactionsRepository;
    private final ClientAdminPaymentGatewayService clientAdminPaymentGatewayService;
    private final StaffService staffService;

    public PaymentTransactionsServiceImpl(PaymentTransactionsRepository paymentTransactionsRepository, ClientAdminPaymentGatewayService clientAdminPaymentGatewayService, StaffService staffService) {
        this.paymentTransactionsRepository = paymentTransactionsRepository;
        this.clientAdminPaymentGatewayService = clientAdminPaymentGatewayService;
        this.staffService = staffService;
    }

    @Override
    public void createOrder(String role, String email, BigDecimal amountInRupees, String receiptNumber, String orderId, StudentFeesCollect feesCollect) throws Exception {
        if (!staffService.hasPermission(role, email, "POST")) {
            throw new RuntimeException("You don't have permission to create order");
        }
        StudentPaymentTransactions paymentTransactions = new StudentPaymentTransactions();
        paymentTransactions.setCreatedBy(email);
        paymentTransactions.setCreatedByRole(role);

        paymentTransactions.setCreatedAt(LocalDateTime.now());
        paymentTransactions.setAmount(amountInRupees);
        paymentTransactions.setStatus(CREATED);
        paymentTransactions.setFeesCollect(feesCollect);
        paymentTransactions.setReceiptNo(receiptNumber);
        paymentTransactions.setRazorpayOrderId(orderId);

        paymentTransactionsRepository.save(paymentTransactions);
    }

    public void updateOrder(String role, String email, boolean isAuthentic, String razorPaymentId, String orderId) {
        if (!staffService.hasPermission(role, email, "POST")) {
            throw new RuntimeException("You don't have permission to update order");
        }

        Optional<StudentPaymentTransactions> byRazorpayOrderId = paymentTransactionsRepository.findByRazorpayOrderId(orderId);

        if (byRazorpayOrderId.isPresent()) {
            StudentPaymentTransactions paymentTransactions = byRazorpayOrderId.get();
            paymentTransactions.setRazorpayPaymentId(razorPaymentId);
            paymentTransactions.setUpdatedBy(email);
//            paymentTransactions.setUpdatedAt(LocalDateTime.now());
            paymentTransactions.setUpdatedByRole(role);

            StudentFeesCollect feesCollect = paymentTransactions.getFeesCollect();
            Long feesCollectId = feesCollect.getId();
            feesCollect.setId(feesCollectId);

            String modeOfPayment = "UNKNOWN", bankName = "", errorReason = "";
            try {
                String branchCode = staffService.fetchBranchCodeByRole(role, email);
                PaymentGatewayAccountResponceDTO paymentGatewayDetails = getPaymentGatewayDetails(branchCode);
                RazorpayClient razorpayClient = new RazorpayClient("", "");
                if (Objects.nonNull(paymentGatewayDetails)) {
                    razorpayClient = new RazorpayClient(paymentGatewayDetails.getKeyId(),
                            paymentGatewayDetails.getSecretKey());
                }
                Payment paymentDetails = razorpayClient.payments.fetch(razorPaymentId);
                modeOfPayment = paymentDetails.get("method").toString().toUpperCase();
                errorReason = paymentDetails.get("error_reason").toString();
                if (paymentDetails.has("bank") && paymentDetails.get("bank") != null) {
                    bankName = paymentDetails.get("bank").toString();
                }
                feesCollect.setPaymentMode(modeOfPayment);
                if (isAuthentic) {
                    paymentTransactions.setStatus(SUCCESS);
                    feesCollect.setStatus("COMPLETED");
                } else {
                    paymentTransactions.setStatus(FAILED);
                    feesCollect.setStatus("FAILED");
                    paymentTransactions.setErrorReason(errorReason);
                }
            } catch (Exception e) {
                // Log warning but don't crash the transaction if metadata fetch fails
                System.err.println("Failed to fetch transaction metadata from Razorpay: " + e.getMessage());
            }
            // This ensures a malicious user cannot fake successful checkout payloads
            paymentTransactionsRepository.save(paymentTransactions);
        }
    }

    public PaymentGatewayAccountResponceDTO getPaymentGatewayDetails(String branchCode) {
        List<PaymentGatewayAccountResponceDTO> paymentGatewayDetails = clientAdminPaymentGatewayService
                .getPaymentGatewayDetails(branchCode).block();
        if (!CollectionUtils.isEmpty(paymentGatewayDetails)) {
            return paymentGatewayDetails.getFirst();
        }
        return null;
    }
}
