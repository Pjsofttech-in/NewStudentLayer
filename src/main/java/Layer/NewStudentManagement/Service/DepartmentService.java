package Layer.NewStudentManagement.Service;


import Layer.NewStudentManagement.DTO.StudentDepartmentDTO;
import Layer.NewStudentManagement.Entity.StudentDepartment;

import java.util.List;

public interface DepartmentService
{
    StudentDepartmentDTO saveDepartment(String role, String email, StudentDepartmentDTO request);
    StudentDepartmentDTO getDepartmentById(Long id, String role, String email);
    StudentDepartmentDTO updateDepartment(Long id,String role,String email,StudentDepartment department);
    void deleteDepartmentById(Long id,String role,String email);
    List<StudentDepartmentDTO> getAllDepartment(String role, String email);
    List<StudentDepartmentDTO> getDepartmentsByDegreeId(String role, String email, Long degreeId, String token);
}
