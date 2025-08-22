package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentSchoolProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolProfileRepository extends JpaRepository<StudentSchoolProfile,Long>
{

    boolean existsByBranchCode(String branchCode);

    @Query("SELECT s FROM StudentSchoolProfile s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    Optional<StudentSchoolProfile> findByBranchCode(@Param("branchCode") String branchCode);

}
