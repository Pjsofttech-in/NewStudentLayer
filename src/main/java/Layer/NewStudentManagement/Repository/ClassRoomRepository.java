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



    @Query("SELECT c FROM StudentClassRoom c " +
            "WHERE (:institutionType IS NULL OR c.institutionType = :institutionType) " +
            "AND (:graduationTypeId IS NULL OR c.graduationType.id = :graduationTypeId) " +
            "AND (:streamId IS NULL OR c.stream.id = :streamId) " +
            "AND (:mediumId IS NULL OR c.medium.id = :mediumId) " +
            "AND (:standardId IS NULL OR c.standard.id = :standardId) " +
            "AND (:degreeNameId IS NULL OR c.degreeName.id = :degreeNameId) " +
            "AND (:departmentName IS NULL OR LOWER(c.departmentName) = LOWER(:departmentName)) " +
            "AND (:groupName IS NULL OR LOWER(c.groupName) = LOWER(:groupName)) " +
            "AND (:year IS NULL OR c.year = :year)")
    List<StudentClassRoom> findByFilters(
            @Param("institutionType") String institutionType,
            @Param("graduationTypeId") Long graduationTypeId,
            @Param("streamId") Long streamId,
            @Param("mediumId") Long mediumId,
            @Param("standardId") Long standardId,
            @Param("degreeNameId") Long degreeNameId,
            @Param("departmentName") String departmentName,
            @Param("groupName") String groupName,
            @Param("year") String year
    );




}
