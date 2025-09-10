package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<StudentAssignment,Long>
{
    @Query("SELECT a FROM StudentAssignment a WHERE a.createdByEmail = :email AND a.role = :role")
    List<StudentAssignment> findAssignmentsByCreator(String email, String role);

    @Query("SELECT a FROM StudentAssignment a WHERE a.classRoom.id = :classRoomId")
    List<StudentAssignment> findByClassRoomId(Long classRoomId);
}
