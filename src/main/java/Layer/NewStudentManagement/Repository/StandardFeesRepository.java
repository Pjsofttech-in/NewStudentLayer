package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentMedium;
import Layer.NewStudentManagement.Entity.StudentStandard;
import Layer.NewStudentManagement.Entity.StudentStandardFees;
import Layer.NewStudentManagement.Entity.StudentStream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StandardFeesRepository extends JpaRepository<StudentStandardFees,Long>
{
    @Query("SELECT s FROM StudentStandardFees s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentStandardFees> findAllByBranchCode(@Param("branchCode") String branchCode);

    @Query("SELECT s FROM StudentStandardFees s WHERE s.standardName = :standardName AND s.mediumName = :mediumName AND s.branchCode = :branchCode")
    List<StudentStandardFees> findByStandardAndMediumAndBranch(String standardName, String mediumName, String branchCode);

    boolean existsByStandardAndMediumAndBranchCode(StudentStandard standard, StudentMedium medium, String branchCode);

}
