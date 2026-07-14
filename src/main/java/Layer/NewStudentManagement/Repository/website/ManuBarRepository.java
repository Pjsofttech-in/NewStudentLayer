package Layer.NewStudentManagement.Repository.website;

import Layer.NewStudentManagement.Entity.website.StudentWebManuBar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManuBarRepository extends JpaRepository<StudentWebManuBar, Long> {

    @Query("SELECT m FROM StudentWebManuBar m WHERE m.branchCode = :branchCode ORDER BY m.id DESC")
    List<StudentWebManuBar> findAllByBranchCode(String branchCode);

    Optional<StudentWebManuBar> findFirstByBranchCode(String branchCode);

}