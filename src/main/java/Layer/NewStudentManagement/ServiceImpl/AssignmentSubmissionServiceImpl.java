package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.AssignmentSubmissionResponseDTO;
import Layer.NewStudentManagement.Entity.StudentAssignment;
import Layer.NewStudentManagement.Entity.StudentAssignmentSubmission;
import Layer.NewStudentManagement.Entity.StudentEntity;
import Layer.NewStudentManagement.Repository.AssignmentRepository;
import Layer.NewStudentManagement.Repository.AssignmentSubmissionRepository;
import Layer.NewStudentManagement.Repository.StudentRepository;
import Layer.NewStudentManagement.Service.AssignmentSubmissionService;
import Layer.NewStudentManagement.Service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentSubmissionServiceImpl implements AssignmentSubmissionService
{
    @Autowired
    AssignmentSubmissionRepository assSubmissionRepo;

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StaffService staffService;

    @Autowired
    S3Service s3Service;

    @Override
    public AssignmentSubmissionResponseDTO submitAssignment(String role, String email, MultipartFile file, StudentAssignmentSubmission request) {
        if (!staffService.hasPermission(role,email,"Post")) {
            throw new RuntimeException("Only STUDENTS can submit assignments!");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        StudentEntity student = studentRepository.findById(request.getStudent().getId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (!student.getEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Unauthorized: You cannot submit for another student!");
        }

        StudentAssignment assignment = assignmentRepository.findById(request.getAssignment().getId())
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        request.setStudent(student);
        request.setAssignment(assignment);
        request.setSubmittedDate(LocalDate.now());
//        request.setStatus("Submitted");
        request.setBranchCode(branchCode);
        request.setCreatedByEmail(email);
        request.setRole(role);


        if (file != null && !file.isEmpty()) {
            String fileUrl = s3Service.uploadFile(file, branchCode);
            request.setFileUrl(fileUrl);
        }

        StudentAssignmentSubmission saved = assSubmissionRepo.save(request);
        return mapToResponse(saved);
    }

    @Override
    public AssignmentSubmissionResponseDTO getSubmission(Long id, String role, String email)
    {
        if (!staffService.hasPermission(role,email,"Get")) {
            throw new RuntimeException("You Don't have Permission to get Assignment!");
        }
        StudentAssignmentSubmission submission = assSubmissionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        if (role.equalsIgnoreCase("STUDENT") &&
                !submission.getStudent().getEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Unauthorized: You can only view your own submissions!");
        }

        return mapToResponse(submission);
    }

    @Override
    public List<AssignmentSubmissionResponseDTO> getSubmissionsByClassRoom(Long classRoomId, String role, String email) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to view assignment submissions!");
        }

        if (role.equalsIgnoreCase("STUDENT")) {
            throw new RuntimeException("Students are not allowed to view submissions by class!");
        }

        return assSubmissionRepo.findByClassRoomId(classRoomId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AssignmentSubmissionResponseDTO updateSubmission(String role, String email, Long id, StudentAssignmentSubmission request) {
        if (!(role.equalsIgnoreCase("STUDENT") || role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("TEACHER"))) {
            throw new RuntimeException("Unauthorized: Cannot update submission!");
        }

        StudentAssignmentSubmission existing = assSubmissionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        if (role.equalsIgnoreCase("STUDENT") &&
                !existing.getStudent().getEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Unauthorized: Cannot update another student's submission!");
        }

        existing.setFileUrl(request.getFileUrl());
        existing.setRemarks(request.getRemarks());
        existing.setSubmittedDate(LocalDate.now());
        existing.setStatus(request.getStatus() != null ? request.getStatus() : existing.getStatus());


        StudentAssignmentSubmission updated = assSubmissionRepo.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void deleteSubmission(Long id,String role, String email) {
        if (!(role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("TEACHER"))) {
            throw new RuntimeException("Only ADMIN/TEACHER can delete submissions!");
        }
        assSubmissionRepo.deleteById(id);
    }

    @Override
    public List<AssignmentSubmissionResponseDTO> getSubmissionsByStudent(
            Long studentId, String role, String email, String filter) {

        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to get Assignment Submissions!");
        }

        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        Long classRoomId = student.getClassRoom().getId();
        List<StudentAssignment> assignments;

        switch (filter == null ? "all" : filter.toLowerCase()) {
            case "submitted":
                assignments = assignmentRepository.findSubmittedAssignments(studentId, classRoomId);
                break;
            case "pending":
                assignments = assignmentRepository.findPendingAssignments(studentId, classRoomId);
                break;
            case "all":
            default:
                assignments = assignmentRepository.findByClassRoomId(classRoomId);
                break;
        }

        return assignments.stream()
                .map(a -> mapAssignmentToResponse(a, studentId))
                .collect(Collectors.toList());

    }

    @Override
    public List<AssignmentSubmissionResponseDTO> getSubmissionsByAssignmentId(Long assignmentId,String role, String email)
    {
        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to get Assignment Submissions!");
        }
        return assSubmissionRepo.findSubmissionsByAssignmentId(assignmentId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    private AssignmentSubmissionResponseDTO mapToResponse(StudentAssignmentSubmission submission) {
        AssignmentSubmissionResponseDTO dto = new AssignmentSubmissionResponseDTO();
        dto.setId(submission.getId());
        dto.setFileUrl(submission.getFileUrl());
        dto.setRemarks(submission.getRemarks());
        dto.setSubmittedDate(submission.getSubmittedDate());
        dto.setStatus(submission.getStatus());
        dto.setStudentId(submission.getStudent().getId());
        dto.setStudentName(submission.getStudent().getFullName());
        dto.setRollNo(submission.getStudent().getRollNo());
        dto.setAssignmentId(submission.getAssignment().getId());
        dto.setAssignmentTitle(submission.getAssignment().getAssignmentTitle());
        dto.setBranchCode(submission.getBranchCode());
        dto.setRole(submission.getRole());
        dto.setCreatedByEmail(submission.getCreatedByEmail());
        return dto;
    }

    private AssignmentSubmissionResponseDTO mapAssignmentToResponse(StudentAssignment assignment, Long studentId) {
        AssignmentSubmissionResponseDTO dto = new AssignmentSubmissionResponseDTO();

        // Assignment info
        dto.setAssignmentId(assignment.getId());
        dto.setAssignmentTitle(assignment.getAssignmentTitle());
        dto.setCreatedByEmail(assignment.getCreatedByEmail());
        dto.setRole(assignment.getRole());
        dto.setBranchCode(assignment.getBranchCode());

        StudentEntity student = studentRepository.findById(studentId).
                orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));


        dto.setStudentId(studentId);
        StudentAssignmentSubmission submission = assignment.getSubmissions()
                .stream()
                .filter(sub -> sub.getStudent().getId().equals(studentId))
                .findFirst()
                .orElse(null);

        if (submission != null) {
            // Submitted
            dto.setStatus("Submitted");
            dto.setFileUrl(submission.getFileUrl());
            dto.setRemarks(submission.getRemarks());
            dto.setSubmittedDate(submission.getSubmittedDate());
            dto.setStudentName(submission.getStudent().getFullName());
            dto.setRollNo(submission.getStudent().getRollNo());
        } else {
            // Pending
            dto.setStatus("Pending");
            dto.setFileUrl(null);
            dto.setRemarks(null);
            dto.setSubmittedDate(null);
            dto.setStudentName(student.getFullName());
            dto.setRollNo(student.getRollNo());
        }

        return dto;
    }

}
