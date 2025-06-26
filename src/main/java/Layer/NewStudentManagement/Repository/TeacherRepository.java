package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<StudentTeacher,Long>
{

    @Query("SELECT s FROM StudentTeacher s WHERE s.teacherEmail=:email")
    Optional<StudentTeacher> findByTeacherEmail(@Param("email") String email);

    @Query("SELECT s FROM StudentTeacher s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentTeacher> findAllByBranchCode(@Param("branchCode") String branchCode);

    boolean existsByTeacherEmail(String teacherEmail);

}
