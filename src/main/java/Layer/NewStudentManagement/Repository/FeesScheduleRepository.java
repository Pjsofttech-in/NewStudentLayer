package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.DTO.FeesScheduleChartProjection;
import Layer.NewStudentManagement.Entity.StudentFeeSchedule;
import Layer.NewStudentManagement.Entity.StudentFees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeesScheduleRepository extends JpaRepository<StudentFeeSchedule,Long>
{

    @Query("SELECT s FROM StudentFeeSchedule s WHERE s.studentFees = :fees AND s.month = :month AND s.feesType = :feesType")
    Optional<StudentFeeSchedule> findByStudentFeesAndLabelAndType(
            @Param("fees") StudentFees fees,
            @Param("month") String label,
            @Param("feesType") String type
    );

    List<StudentFeeSchedule> findByStudentFees_Fid(Long fid);

    @Query("SELECT s FROM StudentFeeSchedule s " +
            "WHERE LOWER(s.month) = LOWER(:month) AND s.studentFees.branchCode = :branchCode")
    List<StudentFeeSchedule> findByMonthAndBranchCode(@Param("month") String month,
                                                      @Param("branchCode") String branchCode);


    @Query(value = """
            SELECT s.month AS monthName, sum(s.collect_amount) AS amount 
            FROM layerstudent.student_fee_schedule s
            JOIN layerstudent.student_fees sf ON s.student_fees_id = sf.fid
            where due_date IS NOT NULL AND YEAR(s.due_date) = :academicYear 
            AND s.is_paid =:isPaid AND sf.branch_code = :branchCode
            group by s.month
            ORDER BY s.month
            """, nativeQuery = true)
    List<FeesScheduleChartProjection> findByAcademicYearAndBranchCode(@Param("academicYear") int year,
                                                                      @Param("isPaid") int isPaid,
                                                                      @Param("branchCode") String branchCode);

    @Query(value = """
            SELECT YEAR(s.due_date) AS year, s.is_paid AS isPaid, sum(s.collect_amount) AS amount
            FROM layerstudent.student_fee_schedule s
            JOIN layerstudent.student_fees sf ON s.student_fees_id = sf.fid
            where due_date IS NOT NULL AND YEAR(s.due_date) >= :fromAcademicYear
            AND YEAR(s.due_date) <= :toAcademicYear AND sf.branch_code = :branchCode
            group by year, s.is_paid
            ORDER BY year
            """, nativeQuery = true)
    List<FeesScheduleChartProjection> findByRangeOfAcademicYearsAndBranchCode(@Param("fromAcademicYear") int fromAcademicYear,
                                                                              @Param("toAcademicYear") int toAcademicYear,
                                                                              @Param("branchCode") String branchCode);
}
