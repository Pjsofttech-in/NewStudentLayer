package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.DTO.StudentCountByCastCategoryDTO;
import Layer.NewStudentManagement.Entity.StudentReligion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReligionRepository extends JpaRepository<StudentReligion,Long>
{
    @Query("SELECT r FROM StudentReligion r WHERE r.student.id = :studentId")
    Optional<StudentReligion> findByStudentId(@Param("studentId") Long studentId);


    @Query("""
    SELECT new Layer.NewStudentManagement.DTO.StudentCountByCastCategoryDTO(
        r.castCategory, COUNT(r)
    )
    FROM StudentReligion r
    JOIN r.student s
    WHERE s.branchCode = :branchCode
      AND (:institutionType IS NULL OR s.institutionType = :institutionType)
      AND (:academicYear IS NULL OR s.academicYear = :academicYear)
    GROUP BY r.castCategory
""")
    List<StudentCountByCastCategoryDTO> getStudentCountByCastCategory(
            @Param("branchCode") String branchCode,
            @Param("institutionType") String institutionType,
            @Param("academicYear") String academicYear
    );


}
