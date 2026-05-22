package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.Entity.StudentFeesCollect;

import java.math.BigDecimal;

public interface PaymentTransactionsService {
    // 1. Create a transaction order
    public String createOrder(String role, String email, BigDecimal amountInRupees, String receiptNumber, StudentFeesCollect feesCollect) throws Exception;
}