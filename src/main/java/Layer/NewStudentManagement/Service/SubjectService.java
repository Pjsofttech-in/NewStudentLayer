package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.StudentSubjectDTO;
import Layer.NewStudentManagement.Entity.StudentSubject;

import java.util.List;

public interface SubjectService
{
    StudentSubject createSubject(String role, String email, StudentSubject subject);
    StudentSubjectDTO getSubjectById(Long id,String role,String email);
    StudentSubject updateSubject(Long id,String role,String email,StudentSubject subject);
    void deleteSubjectById(Long id,String role,String email);
    List<StudentSubjectDTO> getAllSubject(String role, String email);

}
