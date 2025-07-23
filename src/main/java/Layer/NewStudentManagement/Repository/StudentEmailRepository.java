package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudentEmailRepository extends JpaRepository<StudentEmail,Long>
{

    @Query("SELECT s FROM StudentEmail s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentEmail> getAllEmailByBranchCode(@Param("branchCode") String branchCode);


    @Query("SELECT s FROM StudentEmail s WHERE s.isScheduled = true AND s.isSent = false AND s.sentAt <= :now")
    List<StudentEmail> getPendingScheduledEmails(@Param("now") LocalDateTime now);

}
