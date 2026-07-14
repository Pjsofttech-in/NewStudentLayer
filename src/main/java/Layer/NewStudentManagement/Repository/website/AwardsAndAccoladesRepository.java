package Layer.NewStudentManagement.Repository.website;

import Layer.NewStudentManagement.Entity.website.StudentWebAwardsAndAccolades;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AwardsAndAccoladesRepository extends JpaRepository<StudentWebAwardsAndAccolades, Long> {

    @Query("SELECT a FROM StudentWebAwardsAndAccolades a WHERE a.branchCode = :branchCode ORDER BY a.id DESC")
    List<StudentWebAwardsAndAccolades> findAllByBranchCode(String branchCode);
}