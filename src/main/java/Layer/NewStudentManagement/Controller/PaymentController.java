package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.RazorPayOrderCreationDTO;
import Layer.NewStudentManagement.DTO.RazorPayVerifyPaymentDTO;
import Layer.NewStudentManagement.Service.PaymentTransactionsService;
import Layer.NewStudentManagement.Service.RazorpayService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PaymentController {

    @Autowired
    private RazorpayService razorpayService;

    @Autowired
    private PaymentTransactionsService paymentTransactionsService;

    // Call this before opening the modal box
    @PostMapping("/createPaymentOrderId")
    public ResponseEntity<?> startPaymentFlow(@Valid @RequestBody RazorPayOrderCreationDTO requestDTO) {
        try {
            String razorpayOrderId = razorpayService.createOrder(requestDTO.getRole(), requestDTO.getEmail(),
                    requestDTO.getAmount(), requestDTO.getStudentFeeScheduleId());
            return ResponseEntity.ok(Map.of("orderId", razorpayOrderId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to initialize payment tracking infrastructure.");
        }
    }

    // Call this when the modal returns success verification maps
    @PostMapping("/verifyPaymentReceiptDetails")
    public ResponseEntity<?> verifyTransactionReceipt(@Valid @RequestBody RazorPayVerifyPaymentDTO requestDTO) {
        String email = requestDTO.getEmail();
        String role = requestDTO.getRole();
        String orderId = requestDTO.getRazorpay_order_id();
        String paymentId = requestDTO.getRazorpay_payment_id();
        String signature = requestDTO.getRazorpay_signature();

        boolean isAuthentic = razorpayService.verifyPaymentSignature(role, email, orderId, paymentId, signature);
        paymentTransactionsService.updateOrder(role, email, isAuthentic, paymentId, orderId);
        if (isAuthentic) {
            // Business Logic: 
            // 1. Mark transaction record status = PAID in your SQL table.
            // 2. Dispatch automated transactional communications (e.g., WhatsApp, Email).
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Transaction processing complete."));
        } else {
            return ResponseEntity.badRequest().body("Cryptographic verification failures. Transaction discarded.");
        }
    }
}