package Layer.NewStudentManagement.Repository;
import Layer.NewStudentManagement.Entity.StudentRules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RulesRepository extends JpaRepository<StudentRules,Long>
{

    @Query("SELECT s FROM StudentRules s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentRules> findAllRulesByBranchCode(@Param("branchCode") String branchCode);


}
