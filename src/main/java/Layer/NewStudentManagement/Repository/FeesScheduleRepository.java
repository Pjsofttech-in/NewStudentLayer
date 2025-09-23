package Layer.NewStudentManagement.Repository;

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

}
