package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.RazorpayVerifyRequest;
import Layer.NewStudentManagement.Entity.PaymentGatewayAccountResponceDTO;
import Layer.NewStudentManagement.Entity.StudentFeesCollect;
import Layer.NewStudentManagement.Service.FeesCollectService;
import Layer.NewStudentManagement.Service.PaymentTransactionsService;
import Layer.NewStudentManagement.Service.RazorpayService;
import Layer.NewStudentManagement.Util.CryptoUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class RazorpayServiceImpl implements RazorpayService {
    private final StaffService staffService;
    private final FeesCollectService feesCollectService;
    private final PaymentTransactionsService paymentTransactionsService;
    private final ClientAdminPaymentGatewayService clientAdminPaymentGatewayService;
    private static final String SYSTEM = "Student Management Software";


    public RazorpayServiceImpl(CryptoUtil cryptoUtil, StaffService staffService, FeesCollectService feesCollectService,
                               PaymentTransactionsService paymentTransactionsService, ClientAdminPaymentGatewayService clientAdminPaymentGatewayService) {
        this.staffService = staffService;
        this.feesCollectService = feesCollectService;
        this.paymentTransactionsService = paymentTransactionsService;
        this.clientAdminPaymentGatewayService = clientAdminPaymentGatewayService;
    }

    // 1. Create a transaction order
    public String createOrder(String role, String email, BigDecimal amountInRupees, Long studentFeeScheduleId, Long studentMiscFeeId) throws Exception {
        if (!staffService.hasPermission(role, email, "POST")) {
            throw new RuntimeException("You don't have permission to create order");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        PaymentGatewayAccountResponceDTO paymentGatewayDetails = getPaymentGatewayDetails(branchCode);

        //Creating fee collect entry b4 payment gateway in case payment fails
        StudentFeesCollect feesCollect = feesCollectService.createFeeCollectionB4PaymentGateway(
                role, email, null, studentFeeScheduleId, studentMiscFeeId, paymentGatewayDetails, amountInRupees.doubleValue());

        //Calling payment gateway for creating order
        Map<String, Object> order = staffService.createOrder(branchCode, SYSTEM, amountInRupees.longValue());
        String orderId = order.get("orderId").toString();
        String receiptId = String.valueOf(order.get("receiptId"));

        paymentTransactionsService.createOrder(role, email, amountInRupees, receiptId, orderId, feesCollect);

        // Returns the order ID (e.g., order_NXb87tYv8p2Xy)
        return orderId;
    }

    // 2. Cryptographically verify payment signatures sent by frontend
    public boolean verifyPaymentSignature(String role, String email, String orderId, String paymentId, String signature) {
        try {
            if (!staffService.hasPermission(role, email, "POST")) {
                throw new RuntimeException("You don't have permission to create order");
            }

            String branchCode = staffService.fetchBranchCodeByRole(role, email);

            RazorpayVerifyRequest request = new RazorpayVerifyRequest();
            request.setRazorpayOrderId(orderId);
            request.setRazorpayPaymentId(paymentId);
            request.setRazorpaySignature(signature);
            request.setBranchCode(branchCode);
            request.setSystemName(SYSTEM);

            return Boolean.TRUE.equals(staffService.verifyPayment(request).block());
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