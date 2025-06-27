package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentDepartment;
import Layer.NewStudentManagement.Entity.StudentDivision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<StudentDepartment,Long>
{

    @Query("SELECT s FROM StudentDepartment s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentDepartment> findAllByBranchCode(@Param("branchCode") String branchCode);


    @Query("SELECT d FROM StudentDepartment d WHERE d.degreeName.id = :degreeId")
    List<StudentDepartment> findByDegreeNameId(@Param("degreeId") Long degreeId);

    @Query("SELECT s.id FROM StudentDepartment s WHERE s.departmentName = :departmentName")
    Optional<Long> findIdByName(@Param("departmentName") String departmentName);
}

