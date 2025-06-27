package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StandardRepository extends JpaRepository<StudentStandard,Long>
{
    @Query("SELECT s FROM StudentStandard s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentStandard> getAllStandardByBranchCode(@Param("branchCode")String branchCode);

    @Query("SELECT s.id FROM StudentStandard s WHERE s.standardName = :standard")
    Optional<Long> findIdByName(@Param("standard") String standard);
}
