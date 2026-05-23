package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.PaymentTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentTransactionsRepository extends JpaRepository<PaymentTransactions, Long> {
//    @Query("SELECT s FROM StudentPeriod s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
//    List<PaymentTransactions> getAllByBranchCode(@Param("branchCode") String branchCode);

    Optional<PaymentTransactions> findByRazorpayOrderId(String orderId);
}
