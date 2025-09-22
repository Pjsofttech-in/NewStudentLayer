package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentExam;
import Layer.NewStudentManagement.Entity.StudentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<StudentResult,Long>
{

    @Query("SELECT s FROM StudentResult s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentResult> findResultByBranchCode(@Param("branchCode") String branchCode);

    @Query("SELECT r FROM StudentResult r " +
            "WHERE r.student.id = :studentId " +
            "AND r.exam.examDate BETWEEN :startDate AND :endDate")
    List<StudentResult> findResultsForAcademicYear(
            @Param("studentId") Long studentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT r FROM StudentResult r " +
            "WHERE r.student.id = :studentId " +
            "ORDER BY r.exam.examDate DESC")
    List<StudentResult> findLatestResultByStudentIdByExamDate(@Param("studentId") Long studentId);


    @Query("SELECT r FROM StudentResult r " +
            "WHERE r.exam.classRoom.id = :classRoomId")
    List<StudentResult> findByClassRoomId(@Param("classRoomId") Long classRoomId);

}
