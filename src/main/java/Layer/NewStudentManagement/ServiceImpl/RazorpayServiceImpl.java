package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentFeesCollect;
import Layer.NewStudentManagement.Service.FeesCollectService;
import Layer.NewStudentManagement.Service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RazorpayServiceImpl implements RazorpayService {
    private final RazorpayClient razorpayClient;
    private final String keySecret;
    private final StaffService staffService;
    private final FeesCollectService feesCollectService;

    public RazorpayServiceImpl(
            RazorpayClient razorpayClient,
            @Value("${razorpay.key.secret}") String keySecret, StaffService staffService, FeesCollectService feesCollectService) {
        this.razorpayClient = razorpayClient;
        this.keySecret = keySecret;
        this.staffService = staffService;
        this.feesCollectService = feesCollectService;
    }

    // 1. Create a transaction order
    public String createOrder(String role, String email, BigDecimal amountInRupees, Long studentFeeScheduleId) throws Exception {
        if (!staffService.hasPermission(role, email, "POST")) {
            throw new RuntimeException("You don't have permission to create order");
        }
        // Convert Rupees to Paise
        BigDecimal amountInPaise = amountInRupees.multiply(BigDecimal.valueOf(100));

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise.intValue());
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "receiptNumber");//Pls fix this b4 commiting

        StudentFeesCollect feeCollectionB4PaymentGateway = feesCollectService.createFeeCollectionB4PaymentGateway(role, email, studentFeeScheduleId);

        // Call Razorpay API
        Order order = razorpayClient.orders.create(orderRequest);

        // Returns the order ID (e.g., order_NXb87tYv8p2Xy)
        return order.get("id").toString();
    }

    // 2. Cryptographically verify payment signatures sent by frontend
    public boolean verifyPaymentSignature(String role, String email, String orderId, String paymentId, String signature) {
        try {
            if (!staffService.hasPermission(role, email, "POST")) {
                throw new RuntimeException("You don't have permission to create order");
            }
            JSONObject options = new JSONObject();
            options.append("razorpay_order_id", orderId);
            options.append("razorpay_payment_id", paymentId);
            options.append("razorpay_signature", signature);

            // This ensures a malicious user cannot fake successful checkout payloads
            return Utils.verifyPaymentSignature(options, keySecret);
        } catch (Exception e) {
            return false;
        }
    }
}