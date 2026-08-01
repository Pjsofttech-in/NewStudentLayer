package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StandardFeesRepository extends JpaRepository<StudentStandardFees,Long> {
    @Query("SELECT s FROM StudentStandardFees s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentStandardFees> findAllByBranchCode(@Param("branchCode") String branchCode);

    @Query("SELECT s FROM StudentStandardFees s WHERE s.standardName = :standardName AND s.mediumName = :mediumName AND s.branchCode = :branchCode")
    List<StudentStandardFees> findByStandardAndMediumAndBranch(String standardName, String mediumName, String branchCode);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM StudentStandardFees f " +
            "WHERE f.standard = :standard AND f.medium = :medium AND f.branchCode = :branchCode " +
            "AND f.academicYear = :academicYear AND f.institutionType = 'School'")
    boolean existsByStandardAndMediumAndBranchCodeAndAcademicYear(StudentStandard standard,
                                                                  StudentMedium medium,
                                                                  String branchCode,
                                                                  String academicYear);

    // Jr. College: standard + stream + medium + branchCode + academicYear + groupName
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM StudentStandardFees f " +
            "WHERE f.standard = :standard AND f.stream = :stream AND f.medium = :medium AND " +
            "f.branchCode = :branchCode AND f.academicYear = :academicYear AND f.groupName = :groupName " +
            "AND f.institutionType = 'College' AND f.graduationType.graduationType = 'Jr.College'")
    boolean existsByStandardAndStreamAndMediumAndBranchCodeAndAcademicYearAndGroupName(
            StudentStandard standard,
            StudentStream stream,
            StudentMedium medium,
            String branchCode,
            String academicYear,
            String groupName
    );

    // Certification Course: stream + medium + branchCode + academicYear + certification
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM StudentStandardFees f " +
            "WHERE f.stream = :stream AND f.medium = :medium AND " +
            "f.branchCode = :branchCode AND f.academicYear = :academicYear AND f.certification = :certification " +
            "AND f.institutionType = 'College' ")
    boolean existsByStreamAndMediumAndBranchCodeAndAcademicYearAndCertification(
            StudentStream stream,
            StudentMedium medium,
            String branchCode,
            String academicYear,
            StudentCertification certification
    );

    // Diploma: graduationType + courseType + department + medium + branchCode + academicYear
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM StudentStandardFees f " +
            "WHERE f.graduationType = :graduationType AND f.courseType = :courseType AND f.stream = :stream " +
            "AND f.departmentName = :departmentName  AND f.medium = :medium AND f.branchCode = :branchCode " +
            "AND f.academicYear = :academicYear AND f.institutionType = 'College'")
    boolean existsByGraduationTypeAndStreamAndCourseTypeAndDepartmentAndMediumAndBranchCodeAndAcademicYear(
            StudentGraduationType graduationType,
            StudentCourseType courseType,
            String departmentName,
            StudentStream stream,
            StudentMedium medium,
            String branchCode,
            String academicYear
    );

    // UG/PG: graduationType + degree + department + medium + branchCode + academicYear
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM StudentStandardFees f " +
            "WHERE f.graduationType = :graduationType AND f.degree = :degree " +
            "AND f.departmentName = :departmentName AND f.stream = :stream " +
            "AND f.medium = :medium AND f.branchCode = :branchCode AND f.academicYear = :academicYear " +
            "AND f.institutionType = 'College'")
    boolean existsByGraduationTypeAndDegreeAndDepartmentAndMediumAndBranchCodeAndAcademicYear(
            @Param("graduationType") StudentGraduationType graduationType,
            @Param("degree") StudentDegreeName degree,
            @Param("departmentName") String departmentName,
            @Param("stream") StudentStream stream,
            @Param("medium") StudentMedium medium,
            @Param("branchCode") String branchCode,
            @Param("academicYear") String academicYear
    );

    @Query("SELECT f FROM StudentStandardFees f WHERE f.standard.id = :standardId AND f.medium.id = :mediumId AND f.institutionType = 'School' AND f.branchCode = :branchCode")
    List<StudentStandardFees> findForSchool(Long standardId, Long mediumId, String branchCode);

    @Query("SELECT f FROM StudentStandardFees f WHERE f.standard.id = :standardId AND f.medium.id = :mediumId AND f.stream.id = :streamId AND f.graduationType.id = :graduationTypeId AND f.groupName = :groupName AND f.institutionType = 'College' AND f.branchCode = :branchCode")
    List<StudentStandardFees> findForJrCollege(Long standardId, Long mediumId, Long streamId, Long graduationTypeId, String groupName, String branchCode);

    @Query("SELECT f FROM StudentStandardFees f WHERE f.medium.id = :mediumId AND f.stream.id = :streamId AND f.degree.id = :degreeId AND f.departmentName = :departmentName AND f.institutionType = 'College' AND f.branchCode = :branchCode")
    List<StudentStandardFees> findForUGPG(Long mediumId, Long streamId, Long degreeId, String departmentName,String branchCode);

    @Query("SELECT f FROM StudentStandardFees f WHERE f.medium.id = :mediumId AND f.stream.id = :streamId AND f.courseType.id = :courseTypeId AND f.departmentName = :departmentName AND f.institutionType = 'College' AND f.branchCode = :branchCode")
    List<StudentStandardFees> findForDiploma(Long mediumId, Long streamId, Long courseTypeId, String departmentName,String branchCode);

}