package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentReligion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReligionRepository extends JpaRepository<StudentReligion,Long>
{
    @Query("SELECT r FROM StudentReligion r WHERE r.student.id = :studentId")
    Optional<StudentReligion> findByStudentId(@Param("studentId") Long studentId);

}
