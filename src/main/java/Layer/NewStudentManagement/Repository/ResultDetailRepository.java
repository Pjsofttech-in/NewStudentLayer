package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentResultDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultDetailRepository extends JpaRepository<StudentResultDetail,Long>
{

    @Query("SELECT d FROM StudentResultDetail d " +
            "WHERE d.studentResult.id = :resultId " +
            "AND d.subject.id = :subjectId")
    Optional<StudentResultDetail> findByStudentResultIdAndSubjectId(
            @Param("resultId") Long resultId,
            @Param("subjectId") Long subjectId
    );

}
