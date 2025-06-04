package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentEntity;
import Layer.NewStudentManagement.Entity.StudentStream;
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

}
