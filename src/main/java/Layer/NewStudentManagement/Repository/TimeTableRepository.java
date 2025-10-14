package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentTimetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeTableRepository extends JpaRepository<StudentTimetable,Long>
{

    @Query("SELECT t FROM StudentTimetable t WHERE t.classroom.id = :classroomId")
    List<StudentTimetable> findByClassId(@Param("classroomId") Long classroomId);

    @Query("SELECT t FROM StudentTimetable t WHERE t.branchCode = :branchCode")
    List<StudentTimetable> findAllByBranchCode(String branchCode);

}
