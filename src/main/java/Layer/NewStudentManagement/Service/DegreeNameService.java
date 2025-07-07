package Layer.NewStudentManagement.Service;


import Layer.NewStudentManagement.DTO.StudentDegreeNameDTO;
import Layer.NewStudentManagement.Entity.StudentDegreeName;

import java.util.List;

public interface DegreeNameService
{
    StudentDegreeNameDTO saveDegreeName(String role, String email, StudentDegreeNameDTO request);
    StudentDegreeNameDTO getDegreeNameById(Long id, String role, String email);
    StudentDegreeNameDTO updateDegreeName(Long id,String role,String email,StudentDegreeName degreeName);
    void deleteDegreeNameById(Long id,String role,String email);
    List<StudentDegreeNameDTO> getAllDegreeName(String role, String email);
    List<StudentDegreeNameDTO> getDegreeNamesByGraduationType(String role, String email, Long graduationTypeId, String token);
}
