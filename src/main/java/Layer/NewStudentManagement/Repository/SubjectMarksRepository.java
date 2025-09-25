package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentStream;
import Layer.NewStudentManagement.Entity.StudentSubjectMarks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectMarksRepository extends JpaRepository<StudentSubjectMarks,Long>
{
    @Query("SELECT s FROM StudentSubjectMarks s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentSubjectMarks> findSubjectByBranchCode(@Param("branchCode") String branchCode);

    @Query("SELECT s FROM StudentSubjectMarks s WHERE s.classRoom.id = :classRoomId")
    List<StudentSubjectMarks> findByClassRoomId(@Param("classRoomId") Long classRoomId);

    @Query("SELECT s.subject FROM Layer.NewStudentManagement.Entity.StudentExamSubject s WHERE s.exam.id = :examId")
    List<StudentSubjectMarks> findSubjectsByExamId(@Param("examId") Long examId);
}
