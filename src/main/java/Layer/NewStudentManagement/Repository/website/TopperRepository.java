package Layer.NewStudentManagement.Repository.website;

import Layer.NewStudentManagement.Entity.website.StudentWebTopper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopperRepository extends JpaRepository<StudentWebTopper, Long> {
    @Query("SELECT t FROM StudentWebTopper t WHERE t.branchCode = :branchCode ORDER BY t.topperId DESC")
    List<StudentWebTopper> findAllByBranchCode(String branchCode);
}
