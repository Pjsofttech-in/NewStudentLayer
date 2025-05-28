package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<StudentGroup,Long>
{
    @Query("SELECT s FROM StudentGroup s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentGroup> getAllByBranchCode(@Param("branchCode") String branchCode);
}
