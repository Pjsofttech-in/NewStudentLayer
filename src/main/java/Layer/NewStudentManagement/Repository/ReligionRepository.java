package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentReligion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReligionRepository extends JpaRepository<StudentReligion,Long> {
}
