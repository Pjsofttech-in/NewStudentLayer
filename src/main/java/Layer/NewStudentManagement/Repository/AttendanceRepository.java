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
import java.util.List;
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

    // NEW METHOD: Spring Data JPA uses an underscore '_' to traverse into the child entity's ID
    Optional<StudentAttendance> findByRollNoAndClassroomIdAndScheduledPeriodIdAndDate(
            int rollNo,
            Long classroomId,
            Long scheduledPeriodId,
            LocalDate date
    );


    @Query("SELECT s FROM StudentAttendance s WHERE s.rollNo = :rollNo AND s.date = :date AND s.classroomId = :classroomId")
    Optional<StudentAttendance> findByRollNoAndDateAndClassroomId(@Param("rollNo") String rollNo,
                                                                  @Param("date") LocalDate date,
                                                                  @Param("classroomId") Long classroomId);

    @Query("SELECT sa.rollNo FROM StudentAttendance sa WHERE sa.classroomId = :classroomId AND sa.date = :date")
    List<Integer> findPresentRollNos(@Param("classroomId") Long classroomId, @Param("date") LocalDate date);
//

@Query("SELECT COUNT(sa) FROM StudentAttendance sa WHERE sa.classroomId = :classroomId AND sa.date BETWEEN :startDate AND :endDate")
long countByClassroomIdAndDateRange(@Param("classroomId") Long classroomId,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM StudentAttendance a WHERE a.rollNo = :rollNo AND a.branchCode = :branchCode AND a.classroomId = :classroomId AND a.date BETWEEN :start AND :end")
    List<StudentAttendance> findAttendanceByStudentAndDateRange(@Param("rollNo") int rollNo,
                                                                @Param("branchCode") String branchCode,
                                                                @Param("classroomId") Long classroomId,
                                                                @Param("start") LocalDate start,
                                                                @Param("end") LocalDate end);


    @Query("SELECT COUNT(sa) FROM StudentAttendance sa " +
            "WHERE sa.rollNo = :rollNo AND sa.classroomId = :classroomId " +
            "AND sa.date BETWEEN :startDate AND :endDate")
    Long countPresentByStudentAndDateRange(@Param("rollNo") int rollNo,
                                           @Param("classroomId") Long classroomId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);


}
