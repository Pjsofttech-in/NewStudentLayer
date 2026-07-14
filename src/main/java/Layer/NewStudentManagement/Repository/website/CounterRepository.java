package Layer.NewStudentManagement.Repository.website;

import Layer.NewStudentManagement.Entity.website.StudentWebCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CounterRepository extends JpaRepository<StudentWebCounter, Long> {

    @Query("SELECT c FROM StudentWebCounter c WHERE c.branchCode = :branchCode ORDER BY c.id DESC")
    List<StudentWebCounter> findAllByBranchCode(String branchCode);
}