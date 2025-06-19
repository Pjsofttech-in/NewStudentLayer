package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity,Long>, JpaSpecificationExecutor<StudentEntity>
{

    @Query("SELECT s FROM StudentEntity s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentEntity> findAllByBranchCode(@Param("branchCode") String branchCode);

    @Query("SELECT s FROM StudentEntity s WHERE s.mediumName = :medium AND s.standardName = :standard AND s.academicYear=:year AND s.status=:status ORDER BY s.id DESC")
    List<StudentEntity> findByMediumAndStandard(@Param("medium") String medium, @Param("standard") String standard, @Param("year") String year, @Param("status") String status);

    @Query("SELECT s FROM StudentEntity s WHERE s.classRoom.id = :classRoomId")
    List<StudentEntity> findByClassRoomId(@Param("classRoomId") Long classRoomId);

    @Query("SELECT MAX(s.rollNo) FROM StudentEntity s WHERE s.classRoom.id = :classRoomId")
    Integer findMaxRollNoByClassRoomId(@Param("classRoomId") Long classRoomId);

    @Query("SELECT s FROM StudentEntity s WHERE s.classRoom.id = :classRoomId AND s.rollNo IN :rollNos")
    List<StudentEntity> findByClassRoomIdAndRollNos(@Param("classRoomId") Long classRoomId, @Param("rollNos") List<Integer> rollNos);

    @Query("SELECT s FROM StudentEntity s WHERE s.classRoom.id = :classRoomId AND s.rollNo = :rollNo")
    Optional<StudentEntity> findByClassRoomIdAndRollNo(@Param("classRoomId") Long classRoomId, @Param("rollNo") Integer rollNo);

    @Query("SELECT s FROM StudentEntity s WHERE s.classRoom.id = :classroomId")
    List<StudentEntity> findAllByClassroomId(@Param("classroomId") Long classroomId);

    @Query("SELECT COUNT(s) FROM StudentEntity s WHERE s.classRoom.id = :classroomId")
    Long countByClassroomId(@Param("classroomId") Long classroomId);

    @Query("SELECT s FROM StudentEntity s WHERE s.classRoom.id = :classroomId AND s.rollNo NOT IN :presentRollNos")
    List<StudentEntity> findAbsentStudents(@Param("classroomId") Long classroomId, @Param("presentRollNos") List<Integer> presentRollNos);

    @Query("SELECT COUNT(s) FROM StudentEntity s WHERE s.registrationNumber LIKE ?1%")
    Long countByRegistrationNumberStartingWith(String year);


    @Query("SELECT s FROM StudentEntity s " +
            "LEFT JOIN FETCH s.address " +
            "LEFT JOIN FETCH s.educationList " +
            "LEFT JOIN FETCH s.additionalInfo " +
            "LEFT JOIN FETCH s.religion " +
            "LEFT JOIN FETCH s.sports " +
            "LEFT JOIN FETCH s.documents " +
            "WHERE s.registrationNumber = :registrationNumber")
    Optional<StudentEntity> findByRegistrationNumberWithAllData(@Param("registrationNumber") String registrationNumber);



}
