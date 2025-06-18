package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentSports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SportsRepository extends JpaRepository<StudentSports,Long>
{
    @Query("SELECT s FROM StudentSports s WHERE s.student.id = :studentId")
    Optional<StudentSports> findByStudentId(@Param("studentId") Long studentId);

}
