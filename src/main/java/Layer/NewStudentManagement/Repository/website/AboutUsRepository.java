package Layer.NewStudentManagement.Repository.website;

import Layer.NewStudentManagement.Entity.website.StudentWebAboutUs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AboutUsRepository extends JpaRepository<StudentWebAboutUs, Integer> {

    @Query("SELECT a FROM StudentWebAboutUs a WHERE a.branchCode = :branchCode ORDER BY a.id DESC")
    List<StudentWebAboutUs> findAllByBranchCode(String branchCode);

    Optional<StudentWebAboutUs> findFirstByBranchCode(String branchCode);

}