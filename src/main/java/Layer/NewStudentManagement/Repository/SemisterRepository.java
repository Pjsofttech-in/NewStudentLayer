package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentMedium;
import Layer.NewStudentManagement.Entity.StudentSemister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemisterRepository extends JpaRepository<StudentSemister,Long>
{
    @Query("SELECT s FROM StudentSemister s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentSemister> findAllSemisterByBranchCode(@Param("branchCode") String branchCode);
}
