package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.AssignmentResponseDTO;
import Layer.NewStudentManagement.Entity.StudentAssignment;
import Layer.NewStudentManagement.Entity.StudentAssignmentSubmission;
import Layer.NewStudentManagement.Repository.AssignmentRepository;
import Layer.NewStudentManagement.Service.AssignmentService;
import Layer.NewStudentManagement.Service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    StaffService staffService;

    @Autowired
    S3Service s3Service;

    private void validateRole(String role, String expected) {
        if (!role.equalsIgnoreCase(expected)) {
            throw new RuntimeException("Access Denied: Only " + expected + " can perform this action.");
        }
    }

    private void checkPermission(String role, String email, String action) {
        if (!staffService.hasPermission(role, email, action)) {
            throw new RuntimeException("You don't have permission to " + action.toLowerCase() + " student");
        }
    }


    private AssignmentResponseDTO mapToDTO(StudentAssignment assignment) {
        AssignmentResponseDTO dto = new AssignmentResponseDTO();
        dto.setId(assignment.getId());
        dto.setAssignmentTitle(assignment.getAssignmentTitle());
        dto.setDescription(assignment.getDescription());
        dto.setDueDate(assignment.getDueDate());
        dto.setImage(assignment.getImage());
        dto.setCreatedDate(assignment.getCreatedDate());
        dto.setClassRoomId(assignment.getClassRoom().getId());
        dto.setCreatedByEmail(assignment.getCreatedByEmail());
        dto.setRole(assignment.getRole());
        dto.setBranchCode(assignment.getBranchCode());
        List<StudentAssignmentSubmission> submissions = assignment.getSubmissions();
        if (!submissions.isEmpty()) {
            Optional<StudentAssignmentSubmission> first = submissions.stream().findFirst();
            first.ifPresent(studentAssignmentSubmission -> dto.setAssignmentStatus(studentAssignmentSubmission.getStatus()));
        } else {
            if (LocalDate.now().isAfter(assignment.getDueDate())) {
                dto.setAssignmentStatus("Overdue");
            }
        }
        return dto;
    }

    @Override
    public AssignmentResponseDTO createAssignment(String role, String email, MultipartFile image, StudentAssignment assignment) {
        validateRole(role, "TEACHER");
        checkPermission(role, email, "Post");

        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        assignment.setCreatedByEmail(email);
        assignment.setCreatedDate(LocalDate.now());
        assignment.setRole(role);
        assignment.setBranchCode(branchCode);

        if (image != null && !image.isEmpty()) {
            String fileUrl = s3Service.uploadFile(image, branchCode);
            assignment.setImage(fileUrl);
        }


        return mapToDTO(assignmentRepository.save(assignment));
    }

    @Override
    public AssignmentResponseDTO updateAssignment(Long id, String role, String email, StudentAssignment assignment) {
        validateRole(role, "TEACHER");
//        checkPermission(role, email, "Put");
        StudentAssignment existing = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (!existing.getCreatedByEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Access Denied: You can update only your own assignments.");
        }

        existing.setAssignmentTitle(assignment.getAssignmentTitle());
        existing.setDescription(assignment.getDescription());
        existing.setDueDate(assignment.getDueDate());
        existing.setImage(assignment.getImage());

        return mapToDTO(assignmentRepository.save(existing));
    }

    @Override
    public void deleteAssignment(Long id, String role, String email) {
        validateRole(role, "TEACHER");
        checkPermission(role, email, "Delete");
        StudentAssignment existing = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (!existing.getCreatedByEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Access Denied: You can delete only your own assignments.");
        }

        assignmentRepository.delete(existing);
    }

    @Override
    public AssignmentResponseDTO getAssignmentById(Long id, String role, String email) {
        validateRole(role, "TEACHER");
        checkPermission(role, email, "Get");
        return assignmentRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
    }

    @Override
    public List<AssignmentResponseDTO> getAssignmentsByClassRoom(Long classRoomId, String role, String email) {

        checkPermission(role, email, "Get");
        return assignmentRepository.findByClassRoomId(classRoomId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentResponseDTO> getAssignmentsByCreator(String role, String email) {

        checkPermission(role, email, "Get");
        return assignmentRepository.findAssignmentsByCreator(email, role)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}
