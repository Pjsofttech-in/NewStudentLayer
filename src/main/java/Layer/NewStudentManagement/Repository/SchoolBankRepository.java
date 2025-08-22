package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentSchoolBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchoolBankRepository extends JpaRepository<StudentSchoolBank,Long>
{
    @Query("SELECT s FROM StudentSchoolBank s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentSchoolBank> findAllByBranchCode(@Param("branchCode") String branchCode);


}
