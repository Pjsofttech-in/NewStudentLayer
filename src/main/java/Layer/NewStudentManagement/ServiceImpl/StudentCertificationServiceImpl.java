package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StudentCertificationDTO;
import Layer.NewStudentManagement.Entity.StudentCertification;
import Layer.NewStudentManagement.Entity.StudentStream;
import Layer.NewStudentManagement.Repository.CertificationRepository;
import Layer.NewStudentManagement.Repository.StreamRepository;
import Layer.NewStudentManagement.Service.StudentCertificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentCertificationServiceImpl implements StudentCertificationService {

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private StreamRepository streamRepository;

    @Autowired
    private StaffService staffService;

    @Override
    public StudentCertificationDTO createCertification(String role, String email, StudentCertificationDTO request) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to create certification");
        }

        StudentStream studentStream = streamRepository.findById(request.getStreamId())
                .orElseThrow(() -> new RuntimeException("Stream not found with ID: " + request.getStreamId()));

        StudentCertification certification = new StudentCertification();
        certification.setCertification(request.getCertification());
        certification.setCreatedByEmail(email);
        certification.setRole(role);
        certification.setBranchCode(staffService.fetchBranchCodeByRole(role, email));
        certification.setStudentStream(studentStream);

        StudentCertification saved = certificationRepository.save(certification);

        return mapToCertificationDTO(saved);
    }

    @Override
    public List<StudentCertificationDTO> getAllCertifications(String role, String email, String branchCode) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to view certifications");
        }
        
        // Assuming your repository has a method like findByBranchCode
        List<StudentCertification> certifications = certificationRepository.findByBranchCode(branchCode);
        return certifications.stream().map(this::mapToCertificationDTO).collect(Collectors.toList());
    }

    @Override
    public StudentCertificationDTO getCertificationById(Long id, String role, String email) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to view this certification");
        }

        StudentCertification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certification not found with ID: " + id));

        return mapToCertificationDTO(certification);
    }

    @Override
    public StudentCertificationDTO updateCertification(Long id, String role, String email, StudentCertificationDTO request) {
        if (!staffService.hasPermission(role, email, "Put")) {
            throw new RuntimeException("You don't have permission to update certification");
        }

        StudentCertification existingCertification = certificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certification not found with ID: " + id));

        StudentStream studentStream = streamRepository.findById(request.getStreamId())
                .orElseThrow(() -> new RuntimeException("Stream not found with ID: " + request.getStreamId()));

        existingCertification.setCertification(request.getCertification());
        existingCertification.setStudentStream(studentStream);
        // Note: Usually createdByEmail, role, and branchCode are kept as originally created, 
        // unless your business logic requires updating them.

        StudentCertification updated = certificationRepository.save(existingCertification);
        return mapToCertificationDTO(updated);
    }

    @Override
    public void deleteCertification(Long id, String role, String email) {
        if (!staffService.hasPermission(role, email, "Delete")) {
            throw new RuntimeException("You don't have permission to delete certification");
        }

        if (!certificationRepository.existsById(id)) {
            throw new RuntimeException("Certification not found with ID: " + id);
        }

        certificationRepository.deleteById(id);
    }

    @Override
    public List<StudentCertificationDTO> getCertificationsByStreamId(Long streamId, String role, String email) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to view certifications");
        }

        // Ensure your StudentCertificationRepository has: List<StudentCertification> findByStudentStreamId(Long streamId);
        List<StudentCertification> certifications = certificationRepository.findByStudentStreamId(streamId);
        return certifications.stream().map(this::mapToCertificationDTO).collect(Collectors.toList());
    }

    // Helper mapping method
    private StudentCertificationDTO mapToCertificationDTO(StudentCertification entity) {
        StudentCertificationDTO dto = new StudentCertificationDTO();
        dto.setId(entity.getId());
        dto.setCertification(entity.getCertification());
        dto.setCreatedByEmail(entity.getCreatedByEmail());
        dto.setRole(entity.getRole());
        dto.setBranchCode(entity.getBranchCode());
        if (entity.getStudentStream() != null) {
            dto.setStreamId(entity.getStudentStream().getId());
        }
        return dto;
    }
}