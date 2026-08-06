package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentMiscFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentMiscFeeRepository extends JpaRepository<StudentMiscFee, Long> {

    // Spring Data JPA automatically traverses the relationship to find misc fees by the StudentFees ID
    List<StudentMiscFee> findByStudentFeesFid(Long studentFeesId);
    
}