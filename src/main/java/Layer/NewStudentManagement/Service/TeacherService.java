package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.StudentTeacherDTO;
import Layer.NewStudentManagement.DTO.TeacherRequestDTO;
import Layer.NewStudentManagement.Entity.StudentTeacher;
import Layer.NewStudentManagement.Security.LoginRequest;
import Layer.NewStudentManagement.Security.LoginResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface TeacherService
{
    StudentTeacherDTO createTeacher(String role, String email, MultipartFile profilePhoto, TeacherRequestDTO dto);
    StudentTeacherDTO getTeacherById(Long id,String role,String email);
    StudentTeacherDTO updateTeacher(Long id,String role,String email,MultipartFile profilePhoto,TeacherRequestDTO teacher);
    void deleteTeacherById(Long id,String role,String email);
    List<StudentTeacherDTO> getAllTeacher(String role, String email);
    LoginResponse login(LoginRequest request);
    List<StudentTeacherDTO> getTeacherByInstitutionType(String role, String email, String institutionType);
    String resetPassword(String email, String otp, String newPassword);
    String verifyOtp(String email, String otp);
    String sendOtp(String email);
    List<StudentTeacherDTO> getTeachers(String role, String email, String institutionType, String graduationTypeName,String streamName,String courseTypeName, String degreeName, String departmentName);
    Map<String, Long> getPassFailCount(String role, String email, Long examId, Long classroomId);

}
