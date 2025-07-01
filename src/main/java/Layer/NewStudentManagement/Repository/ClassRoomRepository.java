package Layer.NewStudentManagement.Repository;


import Layer.NewStudentManagement.Entity.StudentClassRoom;
import Layer.NewStudentManagement.Entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRoomRepository extends JpaRepository<StudentClassRoom,Long>
{

    @Query("SELECT s FROM StudentClassRoom s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentClassRoom> getAllByBranchCode(@Param("branchCode") String branchCode);

//    @Query("SELECT s.classRoom FROM StudentClassRoomTeacherSubject s WHERE s.teacher.id = :teacherId")
//    List<StudentClassRoom> findClassroomsByTeacherId(@Param("teacherId") Long teacherId);


}
