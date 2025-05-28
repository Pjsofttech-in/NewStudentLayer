package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.TeacherRequestDTO;
import Layer.NewStudentManagement.Entity.StudentTeacher;
import Layer.NewStudentManagement.Security.LoginRequest;
import Layer.NewStudentManagement.Security.LoginResponse;

import java.util.List;

public interface TeacherService
{
    StudentTeacher createTeacher(String role, String email,TeacherRequestDTO dto);
    StudentTeacher getTeacherById(Long id,String role,String email);
    StudentTeacher updateTeacher(Long id,String role,String email,TeacherRequestDTO teacher);
    void deleteTeacherById(Long id,String role,String email);
    List<StudentTeacher> getAllTeacher(String role, String email);
    LoginResponse login(LoginRequest request);

}
