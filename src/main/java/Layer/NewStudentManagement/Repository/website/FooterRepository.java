package Layer.NewStudentManagement.Repository.website;

import Layer.NewStudentManagement.Entity.website.StudentWebFooter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FooterRepository extends JpaRepository<StudentWebFooter, Long> {

    @Query("SELECT f FROM StudentWebFooter f WHERE f.branchCode = :branchCode ORDER BY f.id DESC")
    List<StudentWebFooter> findAllByBranchCode(String branchCode);

    Optional<StudentWebFooter> findFirstByBranchCode(String branchCode);

}