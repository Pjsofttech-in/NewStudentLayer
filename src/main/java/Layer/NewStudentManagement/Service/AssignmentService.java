package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.AssignmentResponseDTO;
import Layer.NewStudentManagement.Entity.StudentAssignment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AssignmentService
{
    AssignmentResponseDTO createAssignment(String role, String email, MultipartFile image, StudentAssignment assignment);

    AssignmentResponseDTO updateAssignment(Long id, String role, String email, StudentAssignment assignment);

    void deleteAssignment(Long id, String role, String email);

    AssignmentResponseDTO getAssignmentById(Long id,String role, String email);

    List<AssignmentResponseDTO> getAssignmentsByClassRoom(Long classRoomId,String role, String email);

    List<AssignmentResponseDTO> getAssignmentsByCreator(String role, String email);
}
