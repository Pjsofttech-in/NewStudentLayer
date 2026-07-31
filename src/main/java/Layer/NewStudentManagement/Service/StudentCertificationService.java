package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.StudentCertificationDTO;
import java.util.List;

public interface StudentCertificationService {

    /**
     * Create a new Student Certification.
     */
    StudentCertificationDTO createCertification(String role, String email, StudentCertificationDTO request);

    /**
     * Retrieve all Student Certifications for a specific branch.
     */
    List<StudentCertificationDTO> getAllCertifications(String role, String email, String branchCode);

    /**
     * Retrieve a specific Student Certification by its ID.
     */
    StudentCertificationDTO getCertificationById(Long id, String role, String email);

    /**
     * Update an existing Student Certification.
     */
    StudentCertificationDTO updateCertification(Long id, String role, String email, StudentCertificationDTO request);

    /**
     * Delete a Student Certification by its ID.
     */
    void deleteCertification(Long id, String role, String email);

    /**
     * Retrieve all Student Certifications by Stream ID.
     */
    List<StudentCertificationDTO> getCertificationsByStreamId(Long streamId, String role, String email);
}