package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentAdditionalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdditionalInfoRepository extends JpaRepository<StudentAdditionalInfo, Long>
{

    @Query("SELECT i FROM StudentAdditionalInfo i WHERE i.student.id = :studentId")
    Optional<StudentAdditionalInfo> findByStudentId(@Param("studentId") Long studentId);

}
