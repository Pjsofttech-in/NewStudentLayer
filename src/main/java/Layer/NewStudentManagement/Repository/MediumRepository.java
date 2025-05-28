package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentMedium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediumRepository extends JpaRepository<StudentMedium,Long>
{
    @Query("SELECT s FROM StudentMedium s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentMedium> findAllByBranchCode(@Param("branchCode") String branchCode);
}
