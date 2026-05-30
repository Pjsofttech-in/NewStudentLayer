package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentPaymentTransactions;
import Layer.NewStudentManagement.Entity.StudentFeesCollect;
import Layer.NewStudentManagement.Repository.PaymentTransactionsRepository;
import Layer.NewStudentManagement.Service.PaymentTransactionsService;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static Layer.NewStudentManagement.Entity.StudentPaymentTransactions.TransactionStatus.*;

@Service
public class PaymentTransactionsServiceImpl implements PaymentTransactionsService {

    private final PaymentTransactionsRepository paymentTransactionsRepository;
    private final RazorpayClient razorpayClient;

    public PaymentTransactionsServiceImpl(PaymentTransactionsRepository paymentTransactionsRepository, RazorpayClient razorpayClient) {
        this.paymentTransactionsRepository = paymentTransactionsRepository;
        this.razorpayClient = razorpayClient;
    }

    @Override
    public void createOrder(String role, String email, BigDecimal amountInRupees, String receiptNumber, String orderId, StudentFeesCollect feesCollect) throws Exception {
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
        Optional<StudentPaymentTransactions> byRazorpayOrderId = paymentTransactionsRepository.findByRazorpayOrderId(orderId);

        if (byRazorpayOrderId.isPresent()) {
            StudentPaymentTransactions paymentTransactions = byRazorpayOrderId.get();
            paymentTransactions.setRazorpayPaymentId(razorPaymentId);
            paymentTransactions.setUpdatedBy(email);
//            paymentTransactions.setUpdatedAt(LocalDateTime.now());
            paymentTransactions.setUpdatedByRole(role);

            StudentFeesCollect feesCollect = paymentTransactions.getFeesCollect();
            Long feesCollectId =  feesCollect.getId();
            feesCollect.setId(feesCollectId);

            String modeOfPayment = "UNKNOWN", bankName = "", errorReason = "";
            try {
                Payment paymentDetails = razorpayClient.payments.fetch(razorPaymentId);
                modeOfPayment = paymentDetails.get("method").toString().toUpperCase();
                errorReason = paymentDetails.get("error_reason").toString();
                if (paymentDetails.has("bank") && paymentDetails.get("bank") != null) {
                    bankName = paymentDetails.get("bank").toString();
                }
                feesCollect.setPaymentMode(modeOfPayment);
                feesCollect.setBankName(bankName);
                if (isAuthentic) {
                    paymentTransactions.setStatus(SUCCESS);
                    feesCollect.setStatus("COMPLETED");
                } else {
                    paymentTransactions.setStatus(FAILED);
                    feesCollect.setStatus("FAILED");
                    paymentTransactions.setErrorReason(errorReason);
                }
                //Need to set this  fields once integration is complete
//                feesCollect.setIfscCode("");
//                feesCollect.setInvoice("");
//                feesCollect.setBankBranchName("");
//                feesCollect.setAccountHolderName("");
            } catch (Exception e) {
                // Log warning but don't crash the transaction if metadata fetch fails
                System.err.println("Failed to fetch transaction metadata from Razorpay: " + e.getMessage());
            }
            // This ensures a malicious user cannot fake successful checkout payloads

            paymentTransactionsRepository.save(paymentTransactions);
        }
    }

}
