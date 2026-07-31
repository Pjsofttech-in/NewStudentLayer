package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificationRepository extends JpaRepository<StudentCertification, Long> {
    /**
     * Retrieves a list of Student Certifications based on the branch code.
     *
     * @param branchCode The branch code to filter by.
     * @return List of matching StudentCertifications.
     */
    List<StudentCertification> findByBranchCode(String branchCode);

    /**
     * Retrieves a list of Student Certifications based on the associated Student Stream ID.
     *
     * @param streamId The ID of the student stream to filter by.
     * @return List of matching StudentCertifications.
     */
    List<StudentCertification> findByStudentStreamId(Long streamId);

    /**
     * (Optional) Retrieves a list of Student Certifications based on both branch code and Stream ID.
     * Use this if you need to filter by stream specifically within a certain branch.
     *
     * @param branchCode The branch code to filter by.
     * @param streamId   The ID of the student stream to filter by.
     * @return List of matching StudentCertifications.
     */
    List<StudentCertification> findByBranchCodeAndStudentStreamId(String branchCode, Long streamId);
}
