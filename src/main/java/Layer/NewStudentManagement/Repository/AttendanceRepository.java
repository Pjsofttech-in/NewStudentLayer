package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRepository extends JpaRepository<StudentAttendance,Long>
{

}
