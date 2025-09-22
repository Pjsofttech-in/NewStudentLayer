package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.DTO.FeesByPaymentModeDTO;
import Layer.NewStudentManagement.DTO.FeesRevenueProjection;
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

    @Query("SELECT MAX(e.id) FROM StudentFeesCollect e")
    Long findMaxId();

    @Query("SELECT YEAR(c.paymentDate) as year, SUM(c.amount) as totalPaid " +
            "FROM StudentFeesCollect c " +
            "WHERE c.paymentDate IS NOT NULL " +
            "AND LOWER(c.status) IN ('paid','completed') " +
            "AND c.branchCode = :branchCode " +
            "GROUP BY YEAR(c.paymentDate) " +
            "ORDER BY year ASC")
    List<Object[]> getPaidFeesReportByYear(@Param("branchCode") String branchCode);


    @Query("SELECT FUNCTION('MONTHNAME', c.paymentDate) as monthName, SUM(c.amount) as totalPaid " +
            "FROM StudentFeesCollect c " +
            "WHERE c.paymentDate IS NOT NULL " +
            "AND LOWER(c.status) IN ('paid','complete','completed') " +
            "AND c.branchCode = :branchCode " +
            "AND YEAR(c.paymentDate) = :year " +
            "GROUP BY FUNCTION('MONTHNAME', c.paymentDate), MONTH(c.paymentDate) " +
            "ORDER BY MONTH(c.paymentDate) ASC")
    List<Object[]> getPaidFeesReportByMonth(@Param("year") int year,
                                            @Param("branchCode") String branchCode);

    @Query("SELECT f.standardName, YEAR(c.paymentDate) as year, SUM(c.amount) as totalPaid " +
            "FROM StudentFeesCollect c " +
            "LEFT JOIN c.studentFees f ON f.fid = c.studentFees.fid " +
            "WHERE c.paymentDate IS NOT NULL " +
            "AND LOWER(c.status) IN ('paid','complete','completed') " +
            "AND c.branchCode = :branchCode " +
            "GROUP BY f.standardName, YEAR(c.paymentDate) " +
            "ORDER BY f.standardName ASC")
    List<Object[]> getPaidFeesReportByStandard(@Param("branchCode") String branchCode);



    @Query("SELECT new Layer.NewStudentManagement.DTO.FeesByPaymentModeDTO(s.paymentMode, SUM(s.amount)) " +
            "FROM StudentFeesCollect s " +
            "WHERE s.branchCode = :branchCode " +
            "AND s.studentFees.institutionType = :institutionType " +
            "GROUP BY s.paymentMode")
    List<FeesByPaymentModeDTO> getCollectedFeesByPaymentMode(@Param("branchCode") String branchCode,
                                                             @Param("institutionType") String institutionType);



    @Query("SELECT s.bankName, SUM(s.amount) " +
            "FROM StudentFeesCollect s " +
            "WHERE s.branchCode = :branchCode " +
            "GROUP BY s.bankName")
    List<Object[]> getFeesRevenueByBank(@Param("branchCode") String branchCode);


}
