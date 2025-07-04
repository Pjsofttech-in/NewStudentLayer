package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentEntity;
import Layer.NewStudentManagement.Entity.StudentFees;
import Layer.NewStudentManagement.Entity.StudentStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeesRepository extends JpaRepository<StudentFees,Long>
{

    boolean existsByStudentAndStandard(StudentEntity student, StudentStandard standard);

    @Query("SELECT s FROM StudentFees s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentFees> getAllByBranchCode(@Param("branchCode") String branchCode);

}
