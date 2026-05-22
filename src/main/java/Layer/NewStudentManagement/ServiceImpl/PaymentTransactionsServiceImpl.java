package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.PaymentTransactions;
import Layer.NewStudentManagement.Entity.StudentFeesCollect;
import Layer.NewStudentManagement.Repository.PaymentTransactionsRepository;
import Layer.NewStudentManagement.Service.PaymentTransactionsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentTransactionsServiceImpl implements PaymentTransactionsService {

    private final PaymentTransactionsRepository paymentTransactionsRepository;

    public PaymentTransactionsServiceImpl(PaymentTransactionsRepository paymentTransactionsRepository) {
        this.paymentTransactionsRepository = paymentTransactionsRepository;
    }

    @Override
    public String createOrder(String role, String email, BigDecimal amountInRupees, String receiptNumber, StudentFeesCollect feesCollect) throws Exception {
        PaymentTransactions paymentTransactions = new PaymentTransactions();
        paymentTransactions.setCreatedBy(email);
        paymentTransactions.setAmount(amountInRupees);
        paymentTransactions.setCreatedByRole(role);
//        paymentTransactions.set

        return "";
    }
}
