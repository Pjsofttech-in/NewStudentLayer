package Layer.NewStudentManagement.Repository.website;

import Layer.NewStudentManagement.Entity.website.StudentWebJobCareerOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobCareerOptionRepository extends JpaRepository<StudentWebJobCareerOption, Long> {

    @Query("SELECT j FROM StudentWebJobCareerOption j WHERE j.branchCode = :branchCode ORDER BY j.id DESC")
    List<StudentWebJobCareerOption> findAllByBranchCode(String branchCode);
}