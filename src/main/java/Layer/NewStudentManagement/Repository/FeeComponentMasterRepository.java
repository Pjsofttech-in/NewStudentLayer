package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentFeeComponentsMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeComponentMasterRepository extends JpaRepository<StudentFeeComponentsMaster, Long>,
        JpaSpecificationExecutor<StudentFeeComponentsMaster> {

}
