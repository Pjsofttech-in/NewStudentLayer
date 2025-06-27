package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentDepartment;
import Layer.NewStudentManagement.Entity.StudentGraduationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GraduationTypeRepository extends JpaRepository<StudentGraduationType,Long>
{

    @Query("SELECT s FROM StudentGraduationType s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentGraduationType> findAllByBranchCode(@Param("branchCode") String branchCode);

    @Query("SELECT g FROM StudentGraduationType g WHERE g.stream.stream = :streamName")
    List<StudentGraduationType> findByStreamName(@Param("streamName") String streamName);

    @Query("SELECT s.id FROM StudentGraduationType s WHERE s.graduationType = :graduationType")
    Optional<Long> findIdByName(@Param("graduationType") String graduationType);
}
