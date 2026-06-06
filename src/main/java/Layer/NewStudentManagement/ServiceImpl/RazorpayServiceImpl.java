package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.PaymentGatewayAccountResponceDTO;
import Layer.NewStudentManagement.Entity.StudentFeesCollect;
import Layer.NewStudentManagement.Service.FeesCollectService;
import Layer.NewStudentManagement.Service.PaymentTransactionsService;
import Layer.NewStudentManagement.Service.RazorpayService;
import Layer.NewStudentManagement.Util.CryptoUtil;
import Layer.NewStudentManagement.Util.ReceiptUtils;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class RazorpayServiceImpl implements RazorpayService {
    private final CryptoUtil cryptoUtil;
    private final StaffService staffService;
    private final FeesCollectService feesCollectService;
    private final PaymentTransactionsService paymentTransactionsService;
    private final ClientAdminPaymentGatewayService clientAdminPaymentGatewayService;

    public RazorpayServiceImpl(CryptoUtil cryptoUtil, StaffService staffService, FeesCollectService feesCollectService,
                               PaymentTransactionsService paymentTransactionsService, ClientAdminPaymentGatewayService clientAdminPaymentGatewayService) {
        this.cryptoUtil = cryptoUtil;
        this.staffService = staffService;
        this.feesCollectService = feesCollectService;
        this.paymentTransactionsService = paymentTransactionsService;
        this.clientAdminPaymentGatewayService = clientAdminPaymentGatewayService;
    }

    // 1. Create a transaction order
    public String createOrder(String role, String email, BigDecimal amountInRupees, Long studentFeeScheduleId) throws Exception {
        if (!staffService.hasPermission(role, email, "POST")) {
            throw new RuntimeException("You don't have permission to create order");
        }
        // Convert Rupees to Paise
        BigDecimal amountInPaise = amountInRupees.multiply(BigDecimal.valueOf(100));
        String receiptId = ReceiptUtils.generateTimestampReceiptId();
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise.intValue());
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receiptId);//Pls fix this b4 commiting

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        PaymentGatewayAccountResponceDTO paymentGatewayDetails = getPaymentGatewayDetails(branchCode);
        RazorpayClient razorpayClient = new RazorpayClient("", "");
        if (Objects.nonNull(paymentGatewayDetails)) {
            razorpayClient = new RazorpayClient(paymentGatewayDetails.getKeyId(),
                    cryptoUtil.decrypt(paymentGatewayDetails.getSecretKey()));
        }
        StudentFeesCollect feesCollect = feesCollectService.createFeeCollectionB4PaymentGateway(role, email, receiptId, studentFeeScheduleId);
        // Call Razorpay API
        Order order = razorpayClient.orders.create(orderRequest);

        paymentTransactionsService.createOrder(role, email, amountInRupees, receiptId, order.get("id").toString(), feesCollect);

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
            options.put("razorpay_order_id", orderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", signature);
            String branchCode = staffService.fetchBranchCodeByRole(role, email);
            PaymentGatewayAccountResponceDTO paymentGatewayDetails = getPaymentGatewayDetails(branchCode);
            String keySecret = "";
            if (Objects.nonNull(paymentGatewayDetails)) {
                keySecret = cryptoUtil.decrypt(paymentGatewayDetails.getSecretKey());
            }
            return Utils.verifyPaymentSignature(options, keySecret);
        } catch (Exception e) {
            return false;
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