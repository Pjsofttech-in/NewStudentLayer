package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentEntranceExam;
import Layer.NewStudentManagement.Entity.StudentMedium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntranceExamRepository extends JpaRepository<StudentEntranceExam,Long>
{

    @Query("SELECT s FROM StudentEntranceExam s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentEntranceExam> findAllByBranchCode(@Param("branchCode") String branchCode);

    @Query("SELECT e FROM StudentEntranceExam e WHERE e.branchCode IN :branchCodes")
    List<StudentEntranceExam> findAllByBranchCodeIn(@Param("branchCodes") List<String> branchCodes);

    @Query("""
            SELECT COUNT(e) > 0
            FROM StudentEntranceExam e
            WHERE LOWER(e.entranceExamName) = LOWER(:examName)
            AND e.branchCode = :branchCode
           """)
    boolean existsIntranceName(@Param("examName") String examName,
                               @Param("branchCode") String branchCode);


}
