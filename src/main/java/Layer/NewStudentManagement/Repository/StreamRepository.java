package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentStream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StreamRepository extends JpaRepository<StudentStream,Long>
{
    @Query("SELECT s FROM StudentStream s WHERE s.branchCode=:branchCode ORDER BY s.id DESC")
    List<StudentStream> findAllByBranchCode(@Param("branchCode") String branchCode);

}
