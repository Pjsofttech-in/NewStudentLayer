package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.ClassRoomRequestDTO;
import Layer.NewStudentManagement.DTO.StudentClassRoomResponseDTO;
import Layer.NewStudentManagement.Entity.StudentClassRoom;

import java.util.List;

public interface ClassRoomService
{

    StudentClassRoomResponseDTO createClassRoom(String role, String email, ClassRoomRequestDTO dto);
    StudentClassRoomResponseDTO updateClassRoom(Long id, String role, String email, StudentClassRoom updateClassRoom);
    StudentClassRoomResponseDTO getClassRoomById(Long id, String role, String email);
    void deleteClassRoomById(Long id, String role, String email);
    List<StudentClassRoomResponseDTO> getAllClassRoom(String role, String email);
    String assignStudentsToClassroom(String role,String email,Long classroomId, List<Long> studentIds);


}
