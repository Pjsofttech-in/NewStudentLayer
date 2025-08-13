package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentEntity;
import Layer.NewStudentManagement.Entity.StudentTcData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TCDataRepository extends JpaRepository<StudentTcData,Long>
{

    @Query("SELECT t.tcNumber FROM StudentTcData t WHERE t.branchCode = :branchCode AND t.tcNumber LIKE CONCAT(:prefix, '%') ORDER BY t.tcNumber DESC")
    List<String> findAllTcNumbersByBranchCodeAndYear(@Param("branchCode") String branchCode, @Param("prefix") String prefix);

    boolean existsByStudent(StudentEntity student);

    @Query("SELECT COUNT(t) > 0 FROM StudentTcData t WHERE t.branchCode = :branchCode AND t.tcNumber = :tcNumber")
    boolean existsByBranchCodeAndTcNumber(@Param("branchCode") String branchCode, @Param("tcNumber") String tcNumber);

    @Query("SELECT t FROM StudentTcData t WHERE t.student.id = :studentId ORDER BY t.tcDate ASC")
    Optional<StudentTcData> findFirstByStudentIdOrderByTcDateAsc(@Param("studentId") Long studentId);


    @Query("SELECT t FROM StudentTcData t WHERE t.branchCode = :branchCode")
    List<StudentTcData> findAllTCByBranchCode(@Param("branchCode") String branchCode);

    @Query("SELECT t FROM StudentTcData t WHERE t.student.id = :studentId")
    List<StudentTcData> findAllByStudentId(@Param("studentId") Long studentId);

    @Query(value = "SELECT * FROM student_tc_data WHERE student_id = :studentId ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<StudentTcData> findLatestByStudentId(@Param("studentId") Long studentId);



}
