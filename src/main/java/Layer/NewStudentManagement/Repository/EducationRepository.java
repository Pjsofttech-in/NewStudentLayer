package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<StudentEducation,Long>
{
    @Query("SELECT e FROM StudentEducation e WHERE e.student.id = :studentId")
    List<StudentEducation> findByStudentId(@Param("studentId") Long studentId);

}
