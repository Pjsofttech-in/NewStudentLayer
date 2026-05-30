package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentPaymentTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentTransactionsRepository extends JpaRepository<StudentPaymentTransactions, Long> {
//    @Query("SELECT s FROM StudentPeriod s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
//    List<StudentPaymentTransactions> getAllByBranchCode(@Param("branchCode") String branchCode);

    Optional<StudentPaymentTransactions> findByRazorpayOrderId(String orderId);
}
