package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentDuplicateTCRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DuplicateTCRequestRepository extends JpaRepository<StudentDuplicateTCRequest,Long>
{
    @Query("SELECT d FROM StudentDuplicateTCRequest d WHERE d.studentId = :studentId")
    List<StudentDuplicateTCRequest> findByStudentIdCustom(@Param("studentId") Long studentId);


    @Query("SELECT d FROM StudentDuplicateTCRequest d WHERE d.branchCode = :branchCode")
    List<StudentDuplicateTCRequest> findAllTCRequestByBranchCode(@Param("branchCode") String branchCode);

}
