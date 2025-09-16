package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.AssignmentSubmissionResponseDTO;
import Layer.NewStudentManagement.Entity.StudentAssignmentSubmission;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AssignmentSubmissionService
{
    AssignmentSubmissionResponseDTO submitAssignment(String role, String email, MultipartFile file, StudentAssignmentSubmission  request);
    AssignmentSubmissionResponseDTO getSubmission(Long id,String role, String email);
    AssignmentSubmissionResponseDTO updateSubmission(String role, String email, Long id, StudentAssignmentSubmission request);
    void deleteSubmission(Long id,String role, String email);
    List<AssignmentSubmissionResponseDTO> getSubmissionsByClassRoom(Long classRoomId, String role, String email);
    List<AssignmentSubmissionResponseDTO> getSubmissionsByStudent(Long studentId, String role, String email, String filter);
}

