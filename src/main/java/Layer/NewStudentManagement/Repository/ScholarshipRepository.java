package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentScholarship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScholarshipRepository extends JpaRepository<StudentScholarship,Long>
{
    @Query("SELECT s FROM StudentScholarship s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentScholarship> findAllByBranchCode(@Param("branchCode") String branchCode);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM StudentScholarship s " +
            "WHERE LOWER(s.scholarshipName) = LOWER(:name) AND s.branchCode = :branchCode")
    boolean existsScholarship(@Param("name") String name,
                              @Param("branchCode") String branchCode);


}
