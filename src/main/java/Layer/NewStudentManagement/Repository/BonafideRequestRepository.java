package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentBonafideRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BonafideRequestRepository extends JpaRepository<StudentBonafideRequest, Long> {

    // For students: See their own request history
    List<StudentBonafideRequest> findByStudentIdOrderByRequestDateDesc(Long studentId);

    // For staff: See all requests in a specific branch with a specific status (e.g., PENDING)
    List<StudentBonafideRequest> findByBranchCodeAndStatusOrderByRequestDateAsc(String branchCode, String status);
}