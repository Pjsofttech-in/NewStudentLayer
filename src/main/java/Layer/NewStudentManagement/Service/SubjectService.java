package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.StudentSubjectDTO;
import Layer.NewStudentManagement.Entity.StudentSubject;

import java.util.List;

public interface SubjectService
{
    StudentSubjectDTO saveSubject(StudentSubjectDTO dto, String role, String email);
    StudentSubjectDTO getSubjectById(Long id,String role,String email);
    StudentSubject updateSubject(Long id,String role,String email,StudentSubject subject);
    void deleteSubjectById(Long id,String role,String email);
    List<StudentSubjectDTO> getAllSubject(String role, String email);

    List<StudentSubjectDTO> getSubjects(String role, String email, String institutionType,
                                        String graduationTypeName, String streamName,
                                        String degreeName, String departmentName);
    List<StudentSubjectDTO> getSubjectsByTeacherId(String role, String email,Long teacherId);
}
