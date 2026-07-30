package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface PeriodRepository extends JpaRepository<StudentPeriod, Long> {
    @Query("SELECT s FROM StudentPeriod s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentPeriod> getAllByBranchCode(@Param("branchCode") String branchCode);

    // Added branchCode to the derived query method
    boolean existsByBranchCodeAndStartTimeAndEndTime(String branchCode, LocalTime startTime, LocalTime endTime);

}
