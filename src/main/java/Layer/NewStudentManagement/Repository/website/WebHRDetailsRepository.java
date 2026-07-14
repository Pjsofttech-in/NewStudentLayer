package Layer.NewStudentManagement.Repository.website;

import Layer.NewStudentManagement.Entity.website.StudentWebHRDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebHRDetailsRepository extends JpaRepository<StudentWebHRDetails, Long> {

    @Query("SELECT w FROM StudentWebHRDetails w WHERE w.webSecurityUrl.branchCode = :branchCode ORDER BY w.id DESC")
    List<StudentWebHRDetails> findAllByBranchCode(String branchCode);
}