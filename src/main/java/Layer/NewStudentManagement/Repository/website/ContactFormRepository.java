package Layer.NewStudentManagement.Repository.website;

import Layer.NewStudentManagement.Entity.website.StudentWebContactForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactFormRepository extends JpaRepository<StudentWebContactForm, Long> {

    @Query("SELECT c FROM StudentWebContactForm c WHERE c.branchCode = :branchCode ORDER BY c.id DESC")
    List<StudentWebContactForm> findAllByBranchCode(@Param("branchCode") String branchCode);

}