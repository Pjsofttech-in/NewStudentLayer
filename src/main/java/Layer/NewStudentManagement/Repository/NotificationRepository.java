package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentNotification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<StudentNotification,Long>
{
    @Query("SELECT n FROM StudentNotification n WHERE n.branchCode = :branchCode AND n.classRoomId IS NULL")
    List<StudentNotification> getNoticesByBranchCode(@Param("branchCode") String branchCode);

    @Query("SELECT n FROM StudentNotification n WHERE n.branchCode = :branchCode AND n.institutionType = :institutionType " +
            " AND n.classRoomId IS NULL")
    List<StudentNotification> getNoticesByInstitutionType(@Param("branchCode") String branchCode, @Param("institutionType") String institutionType);


    @Query("SELECT n FROM StudentNotification n WHERE n.classRoomId = :classId")
    List<StudentNotification> getNoticesByClassId(@Param("classId") Long classId);

    @Query("SELECT n FROM StudentNotification n WHERE n.branchCode = :branchCode AND n.institutionType IS NULL" +
            " AND n.classRoomId IS NULL AND n.studentId = :studentId")
    List<StudentNotification> findByStudentIdAndBranchCode(Long studentId, String branchCode);
}
