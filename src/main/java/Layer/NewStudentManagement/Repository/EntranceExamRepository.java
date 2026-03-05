package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentEntranceExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntranceExamRepository extends JpaRepository<StudentEntranceExam,Long>
{

}
