package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.DTO.FeesRevenueProjection;
import Layer.NewStudentManagement.Entity.StudentEntity;
import Layer.NewStudentManagement.Entity.StudentFees;
import Layer.NewStudentManagement.Entity.StudentStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FeesRepository extends JpaRepository<StudentFees,Long>, JpaSpecificationExecutor<StudentFees>
{

    boolean existsByStudentAndStandard(StudentEntity student, StudentStandard standard);

    @Query("SELECT s FROM StudentFees s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentFees> getAllByBranchCode(@Param("branchCode") String branchCode);


    // For UG/PG
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM StudentFees f " +
            "WHERE f.student = :student AND f.student.degreeName.id = :degreeId AND f.student.department.id = :departmentId")
    boolean existsUGPGFees(@Param("student") StudentEntity student,
                           @Param("degreeId") Long degreeId,
                           @Param("departmentId") Long departmentId);

    // For Jr. College
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM StudentFees f " +
            "WHERE f.student = :student AND f.standard.standardName = :standardName AND f.streamName = :streamName")
    boolean existsJrCollegeFees(@Param("student") StudentEntity student,
                                @Param("standardName") String standardName,
                                @Param("streamName") String streamName);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM StudentFees f " +
            "WHERE f.student = :student AND f.standard.sid = :standardId AND f.medium.mid = :mediumId")
    boolean existsByStandardAndMedium(@Param("student") StudentEntity student,
                                      @Param("standardId") Long standardId,
                                      @Param("mediumId") Long mediumId);

    @Query("""
        SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END
        FROM StudentFees f
        WHERE f.student = :student
          AND f.standard.sid = :standardId
          AND f.medium.mid = :mediumId
          AND f.stream.id = :streamId
          AND f.group.id = :groupId
    """)
    boolean existsFees(
            @Param("student") StudentEntity student,
            @Param("standardId") Long standardId,
            @Param("mediumId") Long mediumId,
            @Param("streamId") Long streamId,
            @Param("groupId") Long groupId
    );

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM StudentFees f " +
            "WHERE f.student = :student AND f.medium.mid = :mediumId AND f.stream.id = :streamId " +
            "AND f.degree.id = :degreeId AND f.department.id = :departmentId")
    boolean existsByMediumStreamDegreeDepartment(@Param("student") StudentEntity student,
                                                 @Param("mediumId") Long mediumId,
                                                 @Param("streamId") Long streamId,
                                                 @Param("degreeId") Long degreeId,
                                                 @Param("departmentId") Long departmentId);



    @Query("SELECT f FROM StudentFees f WHERE f.student.id = :studentId")
    List<StudentFees> findFeesByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT " +
            "SUM(f.totalamount) AS totalFees, " +
            "SUM(f.paidAmount) AS totalPaid, " +
            "SUM(f.pendingAmount) AS totalPending " +
            "FROM StudentFees f " +
            "WHERE f.student.id = :studentId")
    FeesRevenueProjection getFeesRevenueByStudentId(@Param("studentId") Long studentId);
}
