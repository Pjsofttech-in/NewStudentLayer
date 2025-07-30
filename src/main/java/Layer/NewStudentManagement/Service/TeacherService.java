package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.StudentTeacherDTO;
import Layer.NewStudentManagement.DTO.TeacherRequestDTO;
import Layer.NewStudentManagement.Entity.StudentTeacher;
import Layer.NewStudentManagement.Security.LoginRequest;
import Layer.NewStudentManagement.Security.LoginResponse;

import java.util.List;

public interface TeacherService
{
    StudentTeacherDTO createTeacher(String role, String email, TeacherRequestDTO dto);
    StudentTeacherDTO getTeacherById(Long id,String role,String email);
    StudentTeacher updateTeacher(Long id,String role,String email,TeacherRequestDTO teacher);
    void deleteTeacherById(Long id,String role,String email);
    List<StudentTeacherDTO> getAllTeacher(String role, String email);
    LoginResponse login(LoginRequest request);
    List<StudentTeacherDTO> getTeacherByInstitutionType(String role, String email, String institutionType);
    String resetPassword(String email, String otp, String newPassword);
    String verifyOtp(String email, String otp);
    String sendOtp(String email);
    List<StudentTeacherDTO> getTeachers(String role, String email, String institutionType, String graduationTypeName,String streamName);

}
