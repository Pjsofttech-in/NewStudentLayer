package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity,Long>
{

    @Query("SELECT s FROM StudentEntity s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentEntity> findAllByBranchCode(@Param("branchCode") String branchCode);

    @Query("SELECT s FROM StudentEntity s WHERE s.mediumName = :medium AND s.standard = :standard ORDER BY s.id DESC")
    List<StudentEntity> findByMediumAndStandard(@Param("medium") String medium, @Param("standard") String standard);

    @Query("SELECT s FROM StudentEntity s WHERE s.classRoom.id = :classRoomId")
    List<StudentEntity> findByClassRoomId(@Param("classRoomId") Long classRoomId);

    @Query("SELECT MAX(s.rollNo) FROM StudentEntity s WHERE s.classRoom.id = :classRoomId")
    Integer findMaxRollNoByClassRoomId(@Param("classRoomId") Long classRoomId);


}
