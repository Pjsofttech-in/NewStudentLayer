package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentExamSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamSubjectRepository extends JpaRepository<StudentExamSubject, Long>
{
    @Query("SELECT es FROM StudentExamSubject es " +
            "WHERE es.exam.id = :examId AND es.subject.id = :subjectId")
    Optional<StudentExamSubject> findByExamIdAndSubjectId(@Param("examId") Long examId,
                                                          @Param("subjectId") Long subjectId);

}
