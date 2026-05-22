package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.PaymentTransactions;
import Layer.NewStudentManagement.Entity.StudentPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentTransactionsRepository extends JpaRepository<PaymentTransactions,Long>
{
//    @Query("SELECT s FROM StudentPeriod s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
//    List<PaymentTransactions> getAllByBranchCode(@Param("branchCode") String branchCode);

}
