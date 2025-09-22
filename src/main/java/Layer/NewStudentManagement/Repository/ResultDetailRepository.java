package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentResultDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultDetailRepository extends JpaRepository<StudentResultDetail,Long>
{

}
