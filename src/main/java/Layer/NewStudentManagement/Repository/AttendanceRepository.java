package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentAttendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;


@Repository
public interface AttendanceRepository extends JpaRepository<StudentAttendance,Long>, JpaSpecificationExecutor<StudentAttendance>
{

    @Query("SELECT sa FROM StudentAttendance sa WHERE sa.rollNo = :rollNo AND sa.classroomId = :classroomId AND sa.date = :date")
    Optional<StudentAttendance> findAttendanceByRollNoAndClassroomIdAndDate(
            @Param("rollNo") int rollNo,
            @Param("classroomId") Long classroomId,
            @Param("date") LocalDate date
    );


    @Query("SELECT s FROM StudentAttendance s WHERE s.rollNo = :rollNo AND s.date = :date AND s.classroomId = :classroomId")
    Optional<StudentAttendance> findByRollNoAndDateAndClassroomId(@Param("rollNo") String rollNo,
                                                                  @Param("date") LocalDate date,
                                                                  @Param("classroomId") Long classroomId);


}
