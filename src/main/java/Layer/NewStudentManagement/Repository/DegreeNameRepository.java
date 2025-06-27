package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentDegreeName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DegreeNameRepository extends JpaRepository<StudentDegreeName,Long>
{

    @Query("SELECT s FROM StudentDegreeName s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentDegreeName> findAllByBranchCode(@Param("branchCode") String branchCode);


    @Query("SELECT d FROM StudentDegreeName d WHERE d.graduationType.id = :graduationTypeId")
    List<StudentDegreeName> findByGraduationTypeId(@Param("graduationTypeId") Long graduationTypeId);

    @Query("SELECT s.id FROM StudentDegreeName s WHERE s.degreeName = :degreeName")
    Optional<Long> findIdByName(@Param("degreeName") String degreeName);
}
