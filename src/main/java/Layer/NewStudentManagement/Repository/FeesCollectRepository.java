package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentFeesCollect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Repository
public interface FeesCollectRepository extends JpaRepository<StudentFeesCollect,Long>
{
    @Query("SELECT s FROM StudentFeesCollect s " +
            "WHERE s.studentFees.fid = :feesId " +
            "AND s.paymentDate BETWEEN :startDate AND :endDate " +
            "AND s.status = 'Completed'")
    List<StudentFeesCollect> findCompletedPaymentInMonth(
            @Param("feesId") Long feesId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT s FROM StudentFeesCollect s WHERE s.studentFees.fid = :feesId")
    List<StudentFeesCollect> findAllStudentFeesCollected(@Param("feesId") Long feesId);

    @Query("SELECT sfc FROM StudentFeesCollect sfc " +
            "JOIN sfc.studentFees sf " +
            "WHERE sf.student.id = :studentId")
    List<StudentFeesCollect> findCollectedFeesByStudentId(@Param("studentId") Long studentId);
}
