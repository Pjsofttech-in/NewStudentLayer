package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.BonafideRequestDTO;
import Layer.NewStudentManagement.Entity.StudentBonafideRequest;
import Layer.NewStudentManagement.Entity.StudentEntity;
import Layer.NewStudentManagement.Repository.BonafideRequestRepository;
import Layer.NewStudentManagement.Repository.StudentRepository;
import Layer.NewStudentManagement.Service.BonafideRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BonafideRequestServiceImpl implements BonafideRequestService {

    @Autowired
    private BonafideRequestRepository bonafideRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StaffService staffService;

    @Override
    public BonafideRequestDTO raiseRequest(Long studentId, String reason, String role, String email) {
        if (!"STUDENT".equalsIgnoreCase(role)) {
            throw new RuntimeException("Only students can raise a Bonafide Certificate request.");
        }

        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        // Optional: Prevent raising a new request if one is already pending
        List<StudentBonafideRequest> existingRequests = bonafideRepository.findByStudentIdOrderByRequestDateDesc(studentId);
        boolean hasPending = existingRequests.stream().anyMatch(req -> "PENDING".equalsIgnoreCase(req.getStatus()));
        if (hasPending) {
            throw new RuntimeException("You already have a pending Bonafide request. Please wait for it to be processed.");
        }

        StudentBonafideRequest request = new StudentBonafideRequest();
        request.setStudent(student);
        request.setReason(reason);
        request.setBranchCode(student.getBranchCode());
        request.setStatus("PENDING");

        request.setCreatedByEmail(email);
        request.setCreatedByRole(role);
        request.setUpdatedByEmail(email);
        request.setUpdatedByRole(role);

        StudentBonafideRequest saved = bonafideRepository.save(request);
        return mapToDTO(saved);
    }

    @Override
    public List<BonafideRequestDTO> getMyRequests(Long studentId, String role, String email) {
        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to view Bonafide requests.");
        }
        
        List<StudentBonafideRequest> requests = bonafideRepository.findByStudentIdOrderByRequestDateDesc(studentId);
        return requests.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<BonafideRequestDTO> getPendingRequestsForBranch(String role, String email) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to view Bonafide requests.");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        
        // Fetch all PENDING requests for this staff member's branch
        List<StudentBonafideRequest> requests = bonafideRepository.findByBranchCodeAndStatusOrderByRequestDateAsc(branchCode, "PENDING");
        return requests.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public BonafideRequestDTO processRequest(Long requestId, String status, String remarks, String role, String email) {
        if (!staffService.hasPermission(role, email, "Put")) {
            throw new RuntimeException("You don't have permission to process Bonafide requests.");
        }

        if (!status.equalsIgnoreCase("APPROVED") && !status.equalsIgnoreCase("REJECTED")) {
            throw new IllegalArgumentException("Status must be either APPROVED or REJECTED.");
        }

        StudentBonafideRequest request = bonafideRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with ID: " + requestId));

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            throw new RuntimeException("This request has already been processed.");
        }

        request.setStatus(status.toUpperCase());
        request.setRemarks(remarks);
        request.setProcessedByEmail(email);
        request.setProcessedDate(LocalDate.now());

        request.setUpdatedByRole(role);
        request.setUpdatedByEmail(email);

        StudentBonafideRequest updated = bonafideRepository.save(request);
        return mapToDTO(updated);
    }

    // Helper Mapping Method
    private BonafideRequestDTO mapToDTO(StudentBonafideRequest entity) {
        BonafideRequestDTO dto = new BonafideRequestDTO();
        dto.setId(entity.getId());
        dto.setReason(entity.getReason());
        dto.setRequestDate(entity.getRequestDate());
        dto.setStatus(entity.getStatus());
        dto.setRemarks(entity.getRemarks());
        dto.setProcessedByEmail(entity.getProcessedByEmail());
        dto.setProcessedDate(entity.getProcessedDate());
        dto.setBranchCode(entity.getBranchCode());
        
        if (entity.getStudent() != null) {
            dto.setStudentId(entity.getStudent().getId());
            dto.setStudentName(entity.getStudent().getFullName());
            dto.setStandardName(entity.getStudent().getStandardName());
            dto.setRollNo(entity.getStudent().getRollNo());
        }

        dto.setCreatedByDate(entity.getCreatedByDate());
        dto.setCreatedByEmail(entity.getCreatedByEmail());
        dto.setCreatedByRole(entity.getCreatedByRole());

        dto.setUpdatedByDate(entity.getUpdatedByDate());
        dto.setUpdatedByEmail(entity.getUpdatedByEmail());
        dto.setUpdatedByRole(entity.getUpdatedByRole());

        return dto;
    }
}