package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentAssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<StudentAssignmentSubmission,Long>
{
    @Query("SELECT s FROM StudentAssignmentSubmission s WHERE s.student.id = :studentId")
    List<StudentAssignmentSubmission> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT s FROM StudentAssignmentSubmission s WHERE s.assignment.id = :assignmentId")
    List<StudentAssignmentSubmission> findByAssignmentId(@Param("assignmentId") Long assignmentId);

    @Query("SELECT s FROM StudentAssignmentSubmission s JOIN s.student st JOIN st.classRoom cr WHERE cr.id = :classRoomId")
    List<StudentAssignmentSubmission> findByClassRoomId(@Param("classRoomId") Long classRoomId);

    @Query("SELECT s FROM StudentAssignmentSubmission s WHERE s.assignment.id = :assignmentId")
    List<StudentAssignmentSubmission> findSubmissionsByAssignmentId(Long assignmentId);

    @Query("SELECT s FROM StudentAssignmentSubmission s " +
            "WHERE s.student.id = :studentId " +
            "AND s.assignment.classRoom.id = :classRoomId " +
            "AND s.submittedDate > s.assignment.dueDate")
    List<StudentAssignmentSubmission> findLateAssignments(Long studentId, Long classRoomId);

    @Query("SELECT s FROM StudentAssignmentSubmission s " +
            "WHERE s.student.id = :studentId AND s.assignment.id = :assignmentId")
    Optional<StudentAssignmentSubmission> findSubmissionByStudentAndAssignment(
            @Param("studentId") Long studentId,
            @Param("assignmentId") Long assignmentId
    );


}
