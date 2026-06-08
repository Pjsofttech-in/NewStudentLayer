package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentAcademicYear;
import Layer.NewStudentManagement.Entity.StudentCollegeDetails;
import Layer.NewStudentManagement.Entity.StudentProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentCollegeDetailsRepository extends JpaRepository<StudentCollegeDetails,Long>
{
    @Query("SELECT S FROM StudentCollegeDetails S WHERE S.student.id = :studentId")
    Optional<StudentCollegeDetails> findByStudentId(Long studentId);
}
