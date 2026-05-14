package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.DTO.FeesByPaymentModeDTO;
import Layer.NewStudentManagement.DTO.FeesRevenueProjection;
import Layer.NewStudentManagement.DTO.FeesScheduleChartProjection;
import Layer.NewStudentManagement.Entity.StudentFeeSchedule;
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

    @Query("SELECT DAY(c.paymentDate) as dayOfMonth, SUM(c.amount) as amount " +
            "FROM StudentFeesCollect c " +
            "WHERE c.paymentDate IS NOT NULL " +
            "AND LOWER(c.status) IN ('paid','complete','completed') " +
            "AND c.branchCode = :branchCode " +
            "AND YEAR(c.paymentDate) = :year AND MONTHNAME(c.paymentDate) = :monthName " +
            "GROUP BY DAY(c.paymentDate) " +
            "ORDER BY DAY(c.paymentDate) ")
    List<FeesScheduleChartProjection> getPaidFeesReportByDayInMonth(@Param("year") int year,
                                                                    @Param("monthName") String monthName,
                                                                    @Param("branchCode") String branchCode);

    @Query("SELECT f.standardName, SUM(c.amount) as totalPaid " +
            "FROM StudentFeesCollect c " +
            "LEFT JOIN c.studentFees f ON f.fid = c.studentFees.fid " +
            "WHERE c.paymentDate IS NOT NULL " +
            "AND LOWER(c.status) IN ('paid','complete','completed') " +
            "AND YEAR(c.paymentDate) = :academicYear " +
            "AND c.branchCode = :branchCode " +
            "GROUP BY f.standardName " +
            "ORDER BY f.standardName ASC")
    List<Object[]> getPaidFeesReportByStandard(@Param("branchCode") String branchCode,
                                               @Param("academicYear") int academicYear);

    @Query("""
    SELECT new Layer.NewStudentManagement.DTO.FeesByPaymentModeDTO(
        s.paymentMode,
        SUM(s.amount)
    )
    FROM StudentFeesCollect s
    WHERE (:branchCode IS NULL OR s.branchCode = :branchCode)
      AND LOWER(s.status) IN ('paid','complete','completed')
      AND s.studentFees.institutionType = :institutionType
      AND (:year IS NULL OR YEAR(s.paymentDate) = :year)
    GROUP BY s.paymentMode
""")
    List<FeesByPaymentModeDTO> getCollectedFeesByPaymentMode(
            @Param("branchCode") String branchCode,
            @Param("institutionType") String institutionType,
            @Param("year") Integer year);



    @Query("SELECT s.bankName, SUM(s.amount) " +
            "FROM StudentFeesCollect s " +
            "WHERE s.branchCode = :branchCode " +
            "GROUP BY s.bankName")
    List<Object[]> getFeesRevenueByBank(@Param("branchCode") String branchCode);

    @Query("SELECT c FROM StudentFeesCollect c " +
            "WHERE c.studentFeeSchedule = :schedule AND c.branchCode = :branchCode")
    List<StudentFeesCollect> findByScheduleAndBranchCode(@Param("schedule") StudentFeeSchedule schedule,
                                                         @Param("branchCode") String branchCode);

    @Query(value = """
SELECT
    cr.id AS classId,
    divi.division AS division,
    sf.standard_name AS standardName,
    sf.degree_name AS degreeName,
    sf.department_name AS departmentName,
    sfc.payment_mode AS paymentMode,
    SUM(sfc.amount) AS totalFeesRevenue
FROM student_fees_collect sfc

JOIN student_fees sf
    ON sfc.student_fees_id = sf.fid

LEFT JOIN student_class_room cr
    ON sf.standard_id = cr.standard_id

LEFT JOIN student_division divi
    ON cr.division_id = divi.did

WHERE sfc.branch_code = :branchCode
  AND LOWER(TRIM(sfc.status)) = 'completed'

GROUP BY
    cr.id,
    divi.division,
    sf.standard_name,
    sf.degree_name,
    sf.department_name,
    sfc.payment_mode

ORDER BY cr.id
""", nativeQuery = true)
    List<Object[]> findClassWiseRevenueByPaymentMode(
            @Param("branchCode") String branchCode
    );


    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM StudentFeesCollect s " +
            "WHERE s.paymentDate = :date AND s.branchCode = :branchCode")
    Double getTotalFeesByDateAndBranch(@Param("date") LocalDate date,
                                       @Param("branchCode") String branchCode);


    @Query("SELECT s FROM StudentFeesCollect s " +
            "WHERE s.branchCode = :branchCode " +
            "AND s.paymentDate BETWEEN :startDate AND :endDate")
    List<StudentFeesCollect> findByBranchAndDateRange(String branchCode, LocalDate startDate, LocalDate endDate);



}
