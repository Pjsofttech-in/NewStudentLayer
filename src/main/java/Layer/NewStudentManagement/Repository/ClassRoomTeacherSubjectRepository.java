package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.DTO.SubjectByClassroomProjection;
import Layer.NewStudentManagement.Entity.StudentClassRoomTeacherSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRoomTeacherSubjectRepository extends JpaRepository<StudentClassRoomTeacherSubject, Long> {
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

    @Query(value = """
                SELECT st.id AS subjectId, st.subject AS subjectName FROM student_classroom_teacher_subject s JOIN student_subject st 
                ON s.subject_id = st.id 
                WHERE s.assignment_id IN (SELECT id FROM
                student_class_room_teacher_subject s1 where s1.classroom_id = :classroom_id)
            """, nativeQuery = true)
    List<SubjectByClassroomProjection> getAllSubjectsAssignedToClassroom(@Param("classroom_id") Long classroomId);


}
