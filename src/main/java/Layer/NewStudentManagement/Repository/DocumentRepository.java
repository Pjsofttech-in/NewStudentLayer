package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<StudentDocument,Long>
{
    @Query("SELECT sd FROM StudentDocument sd WHERE sd.student.id = :studentId")
    Optional<StudentDocument> findByStudentId(@Param("studentId") Long studentId);
}
