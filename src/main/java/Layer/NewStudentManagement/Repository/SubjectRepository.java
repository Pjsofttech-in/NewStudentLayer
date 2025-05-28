package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<StudentSubject,Long>
{

    @Query("SELECT s FROM StudentSubject s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentSubject> findAllByBranchCode(@Param("branchCode") String branchCode);

}
