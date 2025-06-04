package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationRepository extends JpaRepository<StudentEducation,Long> {
}
