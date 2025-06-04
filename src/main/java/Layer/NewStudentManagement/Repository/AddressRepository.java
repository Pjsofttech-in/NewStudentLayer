package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<StudentAddress, Long> {
}
