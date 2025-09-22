package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentExam;
import Layer.NewStudentManagement.Entity.StudentSubjectMarks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<StudentExam,Long>
{
    @Query("SELECT s FROM StudentExam s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentExam> findExamByBranchCode(@Param("branchCode") String branchCode);
}
