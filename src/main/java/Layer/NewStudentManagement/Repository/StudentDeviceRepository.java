package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentDeviceEntity;
import Layer.NewStudentManagement.Entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentDeviceRepository
        extends JpaRepository<StudentDeviceEntity, Long> {

    Optional<StudentDeviceEntity> findByFirebaseInstallationId(String fid);

    List<StudentDeviceEntity> findByStudentAndActiveTrue(
            StudentEntity student
    );

    List<StudentDeviceEntity> findByStudentIdAndActiveTrue(
            Long studentId
    );

    boolean existsByFirebaseInstallationId(String fid);

    void deleteByFirebaseInstallationId(String fid);
}