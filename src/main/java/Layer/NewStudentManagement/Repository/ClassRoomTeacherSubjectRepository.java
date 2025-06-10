package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentClassRoomTeacherSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRoomTeacherSubjectRepository extends JpaRepository<StudentClassRoomTeacherSubject,Long>
{
    @Query("SELECT cts FROM StudentClassRoomTeacherSubject cts WHERE cts.classRoom.id = :classRoomId")
    List<StudentClassRoomTeacherSubject> findByClassRoomId(@Param("classRoomId") Long classRoomId);


}
