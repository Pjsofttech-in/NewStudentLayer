package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentAdditionalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalInfoRepository extends JpaRepository<StudentAdditionalInfo, Long>
{
}
