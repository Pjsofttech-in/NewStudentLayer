package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.*;
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

//    boolean existsByStandardAndMediumAndBranchCode(StudentStandard standard, StudentMedium medium, String branchCode);


    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM StudentStandardFees f " +
            "WHERE f.standard = :standard AND f.medium = :medium AND f.branchCode = :branchCode AND f.institutionType = 'School'")
    boolean existsByStandardAndMediumAndBranchCode(StudentStandard standard, StudentMedium medium, String branchCode);

    // Junior College: standard + stream + medium + branchCode
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM StudentStandardFees f " +
            "WHERE f.standard = :standard AND f.stream = :stream AND f.medium = :medium AND f.branchCode = :branchCode AND f.institutionType = 'College' AND f.graduationType.graduationType = 'Jr.College'")
    boolean existsByStandardAndStreamAndMediumAndBranchCode(StudentStandard standard, StudentStream stream, StudentMedium medium, String branchCode);

    // UG/PG: graduationType + degree + department + medium + branchCode
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM StudentStandardFees f " +
            "WHERE f.graduationType = :graduationType AND f.degree = :degree AND f.department = :department " +
            "AND f.medium = :medium AND f.branchCode = :branchCode AND f.institutionType = 'College'")
    boolean existsByGraduationTypeAndDegreeAndDepartmentAndMediumAndBranchCode(
            StudentGraduationType graduationType,
            StudentDegreeName degree,
            StudentDepartment department,
            StudentMedium medium,
            String branchCode
    );
}
