package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<StudentAddress, Long>
{
    @Query("SELECT a FROM StudentAddress a WHERE a.student.id = :studentId")
    Optional<StudentAddress> findByStudentId(@Param("studentId") Long studentId);

}
