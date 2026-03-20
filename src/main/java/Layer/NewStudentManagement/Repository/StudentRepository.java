package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.DTO.*;
import Layer.NewStudentManagement.Entity.StudentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity,Long>, JpaSpecificationExecutor<StudentEntity>
{

    @Query("SELECT s FROM StudentEntity s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentEntity> findAllByBranchCode(@Param("branchCode") String branchCode);

    @Query("SELECT s FROM StudentEntity s WHERE s.mediumName = :medium AND s.standardName = :standard AND s.academicYear=:year AND s.status=:status ORDER BY s.id DESC")
    List<StudentEntity> findByMediumAndStandard(@Param("medium") String medium, @Param("standard") String standard, @Param("year") String year, @Param("status") String status);

    @Query("SELECT s FROM StudentEntity s WHERE s.classRoom.id = :classRoomId")
    List<StudentEntity> findByClassRoomId(@Param("classRoomId") Long classRoomId);

    @Query("SELECT MAX(s.rollNo) FROM StudentEntity s WHERE s.classRoom.id = :classRoomId")
    Integer findMaxRollNoByClassRoomId(@Param("classRoomId") Long classRoomId);

    @Query("SELECT s FROM StudentEntity s WHERE s.classRoom.id = :classRoomId AND s.rollNo IN :rollNos")
    List<StudentEntity> findByClassRoomIdAndRollNos(@Param("classRoomId") Long classRoomId, @Param("rollNos") List<Integer> rollNos);

    @Query("SELECT s FROM StudentEntity s WHERE s.classRoom.id = :classRoomId AND s.rollNo = :rollNo")
    Optional<StudentEntity> findByClassRoomIdAndRollNo(@Param("classRoomId") Long classRoomId, @Param("rollNo") Integer rollNo);

    @Query("SELECT s FROM StudentEntity s WHERE s.classRoom.id = :classroomId")
    List<StudentEntity> findAllByClassroomId(@Param("classroomId") Long classroomId);

    @Query("SELECT COUNT(s) FROM StudentEntity s WHERE s.classRoom.id = :classroomId")
    Long countByClassroomId(@Param("classroomId") Long classroomId);

    @Query("SELECT s FROM StudentEntity s WHERE s.classRoom.id = :classroomId AND s.rollNo NOT IN :presentRollNos")
    List<StudentEntity> findAbsentStudents(@Param("classroomId") Long classroomId, @Param("presentRollNos") List<Integer> presentRollNos);

    @Query("SELECT COUNT(s) FROM StudentEntity s WHERE s.registrationNumber LIKE ?1%")
    Long countByRegistrationNumberStartingWith(String year);

    @Query("SELECT COUNT(s) FROM StudentEntity s WHERE s.applicationNumber LIKE ?1%")
    Long countByApplicationNumberStartingWith(String year);

    @Query("SELECT s FROM StudentEntity s " +
            "LEFT JOIN FETCH s.address " +
            "LEFT JOIN FETCH s.educationList " +
            "LEFT JOIN FETCH s.additionalInfo " +
            "LEFT JOIN FETCH s.religion " +
            "LEFT JOIN FETCH s.sports " +
            "LEFT JOIN FETCH s.documents " +
            "WHERE s.registrationNumber = :registrationNumber")
    Optional<StudentEntity> findByRegistrationNumberWithAllData(@Param("registrationNumber") String registrationNumber);


    boolean existsByClassRoomId(Long classRoomId);


    @Query("SELECT s FROM StudentEntity s " +
            "WHERE s.institutionType = 'School' AND " +
            "s.standard.id = :standardId AND " +
            "s.medium.id = :mediumId AND " +
            "s.academicYear = :academicYear AND " +
            "s.status = 'Approved' AND " +
            "s.classRoom IS NULL")
    Page<StudentEntity> findUnassignedSchoolStudents(Long standardId, Long mediumId, String academicYear, Pageable pageable);


    @Query("SELECT s FROM StudentEntity s " +
            "WHERE s.institutionType = 'College' AND " +
            "s.graduationType.id = :graduationTypeId AND " +
            "s.standard.id = :standardId AND " +
            "s.medium.id = :mediumId AND " +
            "s.stream.id = :streamId AND " +
            "s.groupName = :groupName AND " +
            "s.academicYear = :academicYear AND " +
            "s.status = 'Approved' AND " +
            "s.classRoom IS NULL")
    Page<StudentEntity> findUnassignedJrCollegeStudents(Long graduationTypeId, Long standardId, Long mediumId,
                                                        Long streamId, String groupName, String academicYear,
                                                        Pageable pageable);

    @Query("SELECT s FROM StudentEntity s " +
            "WHERE s.institutionType = 'College' AND " +
            "s.medium.id = :mediumId AND " +
            "s.stream.id = :streamId AND " +
            "s.degreeName.id = :degreeNameId AND " +
            "s.departmentName = :departmentName AND " +
            "s.academicYear = :academicYear AND " +
            "s.status = 'Approved' AND " +
            "s.classRoom IS NULL")
    Page<StudentEntity> findUnassignedUGPGStudents(@Param("mediumId") Long mediumId,
                                 @Param("streamId") Long streamId,
                                 @Param("degreeNameId") Long degreeNameId,
                                 @Param("departmentName") String departmentName,
                                 @Param("academicYear") String academicYear,
                                 Pageable pageable);



    @Query("SELECT COUNT(s) FROM StudentEntity s WHERE s.status = :status AND s.enrollmentDate BETWEEN :startDate AND :endDate")
    Long countByStatusAndDateRange(@Param("status") String status,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(s) FROM StudentEntity s WHERE s.enrollmentDate BETWEEN :startDate AND :endDate")
    Long countTotalByDateRange(@Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);

    boolean existsByRegistrationNumber(String registrationNumber);
    boolean existsByApplicationNumber(String applicationNumber);

    @Query("SELECT COUNT(s) FROM StudentEntity s WHERE s.classRoom.id = :classroomId")
    Long countByClassRoomId(@Param("classroomId") Long classroomId);


    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM StudentEntity s WHERE s.email = :email")
    boolean existsByEmail(@Param("email") String email);


    @Query("SELECT s FROM StudentEntity s WHERE s.email=:email")
    Optional<StudentEntity> findByEmail(@Param("email") String email);


    @Query("SELECT new Layer.NewStudentManagement.DTO.GenderCountResponse(" +
            "SUM(CASE WHEN s.gender = 'Male' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.gender = 'Female' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.gender != 'Male' AND s.gender != 'Female' THEN 1 ELSE 0 END)) " +
            "FROM StudentEntity s " +
            "WHERE (:branchCode IS NULL OR s.branchCode = :branchCode) " +
            "AND (:institutionType IS NULL OR s.institutionType = :institutionType) " +
            "AND (:graduationTypeId IS NULL OR s.graduationType.id = :graduationTypeId) " +
            "AND (:streamId IS NULL OR s.stream.id = :streamId) " +
            "AND (:degreeNameId IS NULL OR s.degreeName.id = :degreeNameId) " +
            "AND (:departmentName IS NULL OR s.departmentName = :departmentName) " +
            "AND (:standardId IS NULL OR s.standard.id = :standardId) " +
            "AND (:mediumId IS NULL OR s.medium.id = :mediumId) " +
            "AND (:groupName IS NULL OR s.groupName = :groupName) " +
            "AND (:academicYear IS NULL OR s.academicYear = :academicYear)") // academicYear filter added
    GenderCountResponse getGenderCountByFilters(
            @Param("branchCode") String branchCode,
            @Param("institutionType") String institutionType,
            @Param("graduationTypeId") Long graduationTypeId,
            @Param("streamId") Long streamId,
            @Param("degreeNameId") Long degreeNameId,
            @Param("departmentName") String departmentName,
            @Param("standardId") Long standardId,
            @Param("mediumId") Long mediumId,
            @Param("groupName") String groupName,
            @Param("academicYear") String academicYear // academicYear added here
    );

    @Query("""
    SELECT c.id AS classRoomId,
           c.division.division AS division,
           COUNT(s.id) AS studentCount
    FROM StudentEntity s
    JOIN s.classRoom c
    LEFT JOIN s.graduationType g
    LEFT JOIN s.medium m
    LEFT JOIN s.stream st
    LEFT JOIN s.degreeName d
    WHERE s.branchCode = :branchCode
      AND (:graduationType IS NULL OR g.graduationType = :graduationType)
      AND (:standardName IS NULL OR s.standardName = :standardName)
      AND (:mediumName IS NULL OR m.mediumName = :mediumName)
      AND (:streamName IS NULL OR st.stream = :streamName)
      AND (:degreeName IS NULL OR d.degreeName = :degreeName)
      AND (:departmentName IS NULL OR s.departmentName = :departmentName)
      AND (:institutionType IS NULL OR c.institutionType = :institutionType)
      AND (:academicYear IS NULL OR s.academicYear = :academicYear)
    GROUP BY c.id, c.division.division
""")
    List<ClassRoomStudentCountProjection> getStudentCountByClassRoomWithFilters(
            @Param("branchCode") String branchCode,
            @Param("graduationType") String graduationType,
            @Param("standardName") String standardName,
            @Param("mediumName") String mediumName,
            @Param("streamName") String streamName,
            @Param("degreeName") String degreeName,
            @Param("departmentName") String departmentName,
            @Param("institutionType") String institutionType,
            @Param("academicYear") String academicYear
    );

    @Query("SELECT s FROM StudentEntity s WHERE s.id = :studentId")
    Optional<StudentEntity> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT s.standard.standardName, s.gender, COUNT(s) " +
            "FROM StudentEntity s " +
            "WHERE s.branchCode = :branchCode " +
            "AND (:academicYear IS NULL OR s.academicYear = :academicYear)"+
            "GROUP BY s.standard.standardName, s.gender")
    List<Object[]> getRawStudentCountByGenderAndStandard(@Param("branchCode") String branchCode,
                                                         @Param("academicYear") String academicYear);

    @Query(value = """
    SELECT DISTINCT
        s.id AS id,
        s.full_name AS fullName,
        s.gender AS gender,
        CAST(s.date_of_birth AS CHAR) AS dateOfBirth,
        s.roll_no AS rollNo,
        s.stream_name AS streamName,
        s.medium_name AS mediumName,
        s.group_name AS groupName,
        s.semister AS semister,
        s.institution_type AS institutionType,
        s.classroom_id AS classId,
        d.division AS division,
        st.standard_name AS standard,
        gt.graduation_type AS graduationType      -- CORRECT COLUMN NAME
    FROM student_entity s
    JOIN student_class_room c ON c.id = s.classroom_id
    JOIN student_division d ON d.did = c.division_id
    JOIN student_class_room_teacher_subject ts ON ts.classroom_id = c.id
    JOIN student_teacher t ON t.id = ts.teacher_id

    LEFT JOIN student_standard st 
        ON st.sid = s.standard_id     -- verify your standard table uses sid

    LEFT JOIN student_graduation_type gt 
        ON gt.id = s.graduation_type_id

    WHERE t.teacher_email = :teacherEmail
      AND DATE_FORMAT(s.date_of_birth, '%m-%d')
          BETWEEN DATE_FORMAT(CURDATE(), '%m-%d')
              AND DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 30 DAY), '%m-%d')
""", nativeQuery = true)
    List<UpcomingBirthdayProjection> getUpcomingBirthdays(@Param("teacherEmail") String teacherEmail);


}
