package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.*;

import java.util.List;
import java.util.Map;

public interface ClassRoomService
{

    StudentClassRoomResponseDTO createClassRoom(String role, String email, ClassRoomRequestDTO dto);
    StudentClassRoomResponseDTO updateClassRoom(Long id, String role,
                                                String email, StudentClassRoomRequestDTO request);
    StudentClassRoomResponseDTO getClassRoomById(Long id, String role, String email);
    void deleteClassRoomById(Long id, String role, String email);
    List<StudentClassRoomResponseDTO> getAllClassRoom(String role, String email);
    Map<Long, String> assignStudentsToClassroom(String role, String email, Long classroomId, List<Long> studentIds);
    List<StudentClassRoomResponseDTO> getClassroomDTOsByTeacherId(Long teacherId,String role, String email);
    List<StudentClassRoomResponseDTO> getClassRoomsByFilter(ClassRoomFilterRequest filter);
    void removeStudentsFromClassroom(String role, String email, Long classroomId, List<Long> studentIds);
    List<TeacherWithSubjectsDTO> getTeachersWithSubjectsByClassroom(String role, String email, Long classroomId);
    List<SubjectByClassroomProjection> getAllSubjectsByClassroom(String role, String email, Long classroomId);
}
