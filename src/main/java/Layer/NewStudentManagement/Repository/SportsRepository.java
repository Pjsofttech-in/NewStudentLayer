package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentSports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SportsRepository extends JpaRepository<StudentSports,Long> {
}
