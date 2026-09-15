package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentTeacher;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<StudentTeacher,Long>
{

    @Query("SELECT s FROM StudentTeacher s WHERE s.teacherEmail=:email AND s.active = true")
    Optional<StudentTeacher> findByTeacherEmail(@Param("email") String email);

    Optional<StudentTeacher> findByIdAndActiveTrue(Long id);

    @Query("SELECT s FROM StudentTeacher s WHERE s.branchCode=:branchCode AND s.active = true ORDER BY s.id DESC")
    List<StudentTeacher> findAllByBranchCode(@Param("branchCode") String branchCode);

    boolean existsByTeacherEmailAndActiveTrue(String teacherEmail);

    @Query("SELECT t FROM StudentTeacher t JOIN t.subjects s WHERE s.id = :subjectId AND t.active = true")
    List<StudentTeacher> findTeachersBySubjectId(@Param("subjectId") Long subjectId);

    @Query("SELECT t FROM StudentTeacher t " +
            "WHERE t.branchCode = :branchCode " +
            "AND t.active = true")
    List<StudentTeacher> findTeachersByFilters(
            @Param("branchCode") String branchCode
    );

    @Modifying
    @Transactional
    @Query("UPDATE StudentTeacher t SET t.active = false WHERE t.id = :id")
    void deactivateTeacherById(@Param("id") Long id);


}
