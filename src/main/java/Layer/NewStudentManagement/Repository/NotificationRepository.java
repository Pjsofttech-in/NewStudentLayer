package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<StudentNotification,Long>
{

    @Query("SELECT n FROM StudentNotification n WHERE n.branchCode = :branchCode AND n.classRoomId IS NULL")
    List<StudentNotification> getNoticesByBranchCode(@Param("branchCode") String branchCode);


    @Query("SELECT n FROM StudentNotification n WHERE n.classRoomId = :classId")
    List<StudentNotification> getNoticesByClassId(@Param("classId") Long classId);
}
