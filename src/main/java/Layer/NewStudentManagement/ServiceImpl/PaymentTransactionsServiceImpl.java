package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.*;
import Layer.NewStudentManagement.Repository.FeesRepository;
import Layer.NewStudentManagement.Repository.FeesScheduleRepository;
import Layer.NewStudentManagement.Repository.PaymentTransactionsRepository;
import Layer.NewStudentManagement.Service.PaymentTransactionsService;
import Layer.NewStudentManagement.Util.CryptoUtil;
import com.razorpay.Order;
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
    private final FeesRepository feesRepository;
    private final FeesScheduleRepository feesScheduleRepository;
    private final StaffService staffService;
    private final CryptoUtil cryptoUtil;

    public PaymentTransactionsServiceImpl(PaymentTransactionsRepository paymentTransactionsRepository, ClientAdminPaymentGatewayService clientAdminPaymentGatewayService, FeesRepository feesRepository, FeesScheduleRepository feesScheduleRepository, StaffService staffService, CryptoUtil cryptoUtil) {
        this.paymentTransactionsRepository = paymentTransactionsRepository;
        this.clientAdminPaymentGatewayService = clientAdminPaymentGatewayService;
        this.feesRepository = feesRepository;
        this.feesScheduleRepository = feesScheduleRepository;
        this.staffService = staffService;
        this.cryptoUtil = cryptoUtil;
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
                            cryptoUtil.decrypt(paymentGatewayDetails.getSecretKey()));
                }
                Payment paymentDetails = razorpayClient.payments.fetch(razorPaymentId);
                modeOfPayment = paymentDetails.get("method").toString().toUpperCase();
                errorReason = paymentDetails.get("error_reason").toString();
                if (paymentDetails.has("bank") && paymentDetails.get("bank") != null) {
                    bankName = paymentDetails.get("bank").toString();
                }
                feesCollect.setPaymentMode(modeOfPayment);
                feesCollect.setTransactionId(paymentTransactions.getReceiptNo());
                if (isAuthentic) {
                    paymentTransactions.setStatus(SUCCESS);
                    feesCollect.setStatus("COMPLETED");

                    StudentFeeSchedule feeSchedule = feesCollect.getStudentFeeSchedule();
                    if(Objects.nonNull(feeSchedule)){
                        feeSchedule.setPaid(true);
                        feesScheduleRepository.save(feeSchedule);

                        if (Objects.nonNull(feeSchedule.getStudentFees())) {
                            StudentFees studentFees = feeSchedule.getStudentFees();
                            double totalAmount = studentFees.getTotalamount();
                            double paidAmount = studentFees.getPaidAmount();
                            double pendingAmount = 0.0;

                            paidAmount += feesCollect.getAmount();
                            pendingAmount = studentFees.getTotalamount() - paidAmount;

                            studentFees.setPendingAmount(pendingAmount);
                            studentFees.setPaidAmount(paidAmount);

                            if(paidAmount >= totalAmount){
                                studentFees.setFeesStatus("Completed");
                            }
                            feesRepository.save(studentFees);
                        }
                    }

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

    @Override
    public void cancelOrderPayment(String role, String email, String orderId) {
        if (!staffService.hasPermission(role, email, "POST")) {
            throw new RuntimeException("You don't have permission to update order");
        }

        // Fetch the transaction using only the Order ID, as Payment ID is null on cancellation
        Optional<StudentPaymentTransactions> byRazorpayOrderId = paymentTransactionsRepository.findByRazorpayOrderId(orderId);

        if (byRazorpayOrderId.isPresent()) {
            StudentPaymentTransactions paymentTransactions = byRazorpayOrderId.get();

            // If it's already processed (Success or Failed), don't override it
            if (paymentTransactions.getStatus() != StudentPaymentTransactions.TransactionStatus.CREATED) {
                return;
            }

            paymentTransactions.setUpdatedBy(email);
            paymentTransactions.setUpdatedByRole(role);

            // Mark as failed due to user cancellation
            paymentTransactions.setStatus(StudentPaymentTransactions.TransactionStatus.FAILED);
            paymentTransactions.setErrorReason("payment_cancelled_by_user");

            StudentFeesCollect feesCollect = paymentTransactions.getFeesCollect();
            if (feesCollect != null) {
                feesCollect.setStatus("FAILED");
            }

//            // Optional: You can still try to fetch the Order from Razorpay to get exact status
//            try {
//                String branchCode = staffService.fetchBranchCodeByRole(role, email);
//                PaymentGatewayAccountResponceDTO paymentGatewayDetails = getPaymentGatewayDetails(branchCode);
//                RazorpayClient razorpayClient = new RazorpayClient("", "");
//
//                if (Objects.nonNull(paymentGatewayDetails)) {
//                    razorpayClient = new RazorpayClient(paymentGatewayDetails.getKeyId(),
//                            cryptoUtil.decrypt(paymentGatewayDetails.getSecretKey()));
//                }
//
//                // Fetch the order to see if Razorpay recorded any attempts
//                Order razorpayOrder = razorpayClient.orders.fetch(orderId);
//                String orderStatus = razorpayOrder.get("status");
//
//                // If the order status is just 'created' or 'attempted' but we are cancelling,
//                // we can add that metadata.
//                System.out.println("Razorpay Order Status during cancellation: " + orderStatus);
//
//            } catch (Exception e) {
//                System.err.println("Failed to fetch order metadata from Razorpay during cancellation: " + e.getMessage());
//            }

            paymentTransactionsRepository.save(paymentTransactions);
        } else {
            throw new RuntimeException("Transaction not found for Order ID: " + orderId);
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
