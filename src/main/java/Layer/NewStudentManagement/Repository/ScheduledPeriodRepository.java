package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentScheduledPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ScheduledPeriodRepository extends JpaRepository<StudentScheduledPeriod,Long>
{
    @Query("SELECT s FROM StudentScheduledPeriod s " +
            "WHERE s.timetable.id = :timetableId " +
            "AND s.subject.id = :subjectId " +
            "AND s.teacher.id = :teacherId " +
            "AND s.periodDate = :date")
    Optional<StudentScheduledPeriod> findByTimetableAndSubjectAndTeacherAndDate(
            @Param("timetableId") Long timetableId,
            @Param("subjectId") Long subjectId,
            @Param("teacherId") Long teacherId,
            @Param("date") LocalDate date
    );

    @Query("SELECT s FROM StudentScheduledPeriod s " +
            "WHERE s.timetable.id = :timetableId " +
            "AND s.subject.id = :subjectId " +
            "AND s.teacher.id = :teacherId")
    Optional<StudentScheduledPeriod> findTemplatePeriod(
            @Param("timetableId") Long timetableId,
            @Param("subjectId") Long subjectId,
            @Param("teacherId") Long teacherId
    );

    @Query("SELECT s FROM StudentScheduledPeriod s " +
            "WHERE s.timetable.id = :timetableId " +
            "AND s.subject.id = :subjectId " +
            "AND s.teacher.id = :teacherId " +
            "AND s.periodSlot.id = :slotId " +
            "AND s.periodDate = :date")
    Optional<StudentScheduledPeriod> findByDateEntry(
            @Param("timetableId") Long timetableId,
            @Param("subjectId") Long subjectId,
            @Param("teacherId") Long teacherId,
            @Param("slotId") Long slotId,
            @Param("date") LocalDate date
    );

    @Query("SELECT s FROM StudentScheduledPeriod s " +
            "WHERE s.timetable.id = :timetableId " +
            "AND s.subject.id = :subjectId " +
            "AND s.teacher.id = :teacherId " +
            "AND s.periodSlot.id = :slotId " +
            "AND s.periodDate IS NULL")
    Optional<StudentScheduledPeriod> findBasePeriod(
            @Param("timetableId") Long timetableId,
            @Param("subjectId") Long subjectId,
            @Param("teacherId") Long teacherId,
            @Param("slotId") Long slotId
    );

    @Query("SELECT s FROM StudentScheduledPeriod s " +
            "WHERE s.timetable.id = :timetableId " +
            "AND s.periodSlot.id = :periodId ")
    Optional<StudentScheduledPeriod> findScheduleByPeriod(
            @Param("timetableId") Long timetableId,
            @Param("periodId") Long periodId
    );
}
