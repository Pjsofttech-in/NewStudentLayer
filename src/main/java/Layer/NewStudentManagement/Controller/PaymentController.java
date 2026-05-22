package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.Service.RazorpayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
public class PaymentController {

    @Autowired
    private RazorpayService razorpayService;

    // Call this before opening the modal box
    @PostMapping("/createPaymentOrderId")
    public ResponseEntity<?> startPaymentFlow(@RequestParam String role,
                                              @RequestParam String email,
                                              @RequestParam BigDecimal amount,
                                              @RequestParam Long studentFeeScheduleId) {
        try {
            String razorpayOrderId = razorpayService.createOrder(role, email, amount, studentFeeScheduleId);
            return ResponseEntity.ok(Map.of("orderId", razorpayOrderId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to initialize payment tracking infrastructure.");
        }
    }

    // Call this when the modal returns success verification maps
    @PostMapping("/verifyPaymentReceiptDetails")
    public ResponseEntity<?> verifyTransactionReceipt(@RequestParam String role,
                                                      @RequestParam String email,
                                                      @RequestBody Map<String, String> payload) {
        String orderId = payload.get("razorpay_order_id");
        String paymentId = payload.get("razorpay_payment_id");
        String signature = payload.get("razorpay_signature");

        boolean isAuthentic = razorpayService.verifyPaymentSignature(role, email, orderId, paymentId, signature);

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