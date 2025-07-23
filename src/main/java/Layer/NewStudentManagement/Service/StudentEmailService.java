package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.EmailRequestDTO;
import Layer.NewStudentManagement.Entity.StudentEmail;

import java.util.List;

public interface StudentEmailService
{
    String sendEmailToStudents(String role, String email,EmailRequestDTO dto);
    List<StudentEmail> getAllSentEmails(String role, String email);
}
