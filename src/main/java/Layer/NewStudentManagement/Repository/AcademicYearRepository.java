package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentAcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcademicYearRepository extends JpaRepository<StudentAcademicYear,Long>
{

    @Query("SELECT a FROM StudentAcademicYear a WHERE a.branchCode=:branchCode ORDER BY a.id DESC")
    List<StudentAcademicYear> findAllByBranchCode(@Param("branchCode")String branchCode);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM StudentAcademicYear a " +
            "WHERE a.academicYear = :academicYear AND a.branchCode = :branchCode")
    boolean existsByAcademicYearAndBranchCode(@Param("academicYear") String academicYear,
                                              @Param("branchCode") String branchCode);

    @Query("SELECT s FROM StudentAcademicYear s WHERE s.branchCode IN :branchCodes")
    List<StudentAcademicYear> findAllByBranchCodeIn(@Param("branchCodes") List<String> branchCodes);
}
