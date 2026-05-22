package Layer.NewStudentManagement.Service;

import java.math.BigDecimal;

public interface RazorpayService {
    // 1. Create a transaction order
    public String createOrder(String role, String email, BigDecimal amountInRupees, Long studentFeeScheduleId) throws Exception;

    // 2. Cryptographically verify payment signatures sent by frontend
    public boolean verifyPaymentSignature(String role, String email, String orderId, String paymentId, String signature);
}