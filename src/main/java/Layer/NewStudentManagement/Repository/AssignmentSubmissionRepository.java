package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentAssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<StudentAssignmentSubmission,Long>
{
    @Query("SELECT s FROM StudentAssignmentSubmission s WHERE s.student.id = :studentId")
    List<StudentAssignmentSubmission> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT s FROM StudentAssignmentSubmission s WHERE s.assignment.id = :assignmentId")
    List<StudentAssignmentSubmission> findByAssignmentId(@Param("assignmentId") Long assignmentId);

    @Query("SELECT s FROM StudentAssignmentSubmission s WHERE s.student.classRoom.id = :classRoomId")
    List<StudentAssignmentSubmission> findByClassRoomId(@Param("classRoomId") Long classRoomId);

}
