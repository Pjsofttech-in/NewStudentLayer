package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.Entity.StudentFeesCollect;

import java.math.BigDecimal;

public interface PaymentTransactionsService {
    // 1. Create a transaction order
    public void createOrder(String role, String email, BigDecimal amountInRupees, String receiptNumber, String orderId, StudentFeesCollect feesCollect) throws Exception;

    public void updateOrder(String role, String email, boolean isAuthentic, String razorPaymentId, String orderId);
}