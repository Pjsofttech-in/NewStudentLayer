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


    @Query("SELECT s FROM StudentClassRoomTeacherSubject s " +
            "JOIN FETCH s.classRoom c " +
            "JOIN FETCH s.teacher t " +
            "LEFT JOIN FETCH s.subjects " +
            "WHERE t.id = :teacherId")
    List<StudentClassRoomTeacherSubject> findAssignmentsByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT s FROM StudentClassRoomTeacherSubject s " +
            "JOIN FETCH s.teacher t " +
            "JOIN FETCH s.subjects sub " +
            "WHERE s.classRoom.id = :classroomId")
    List<StudentClassRoomTeacherSubject> findByClassRoomIdWithSubjects(@Param("classroomId") Long classroomId);

    @Query("""
        SELECT DISTINCT m.classRoom.id
        FROM StudentClassRoomTeacherSubject m
        WHERE m.teacher.teacherEmail = :email
    """)
    List<Long> getClassroomIdsByTeacherEmail(@Param("email") String teacherEmail);


}
