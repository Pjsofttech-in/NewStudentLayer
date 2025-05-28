package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentDivision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DivisionRepository extends JpaRepository<StudentDivision,Long>
{

    @Query("SELECT s FROM StudentDivision s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentDivision> findAllByBranchCode(@Param("branchCode") String branchCode);
}
