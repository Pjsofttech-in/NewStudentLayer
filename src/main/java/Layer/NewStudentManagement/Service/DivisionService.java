package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.StudentDivisionDTO;
import Layer.NewStudentManagement.Entity.StudentDivision;
import jakarta.annotation.Nullable;

import java.util.List;

public interface DivisionService
{
    StudentDivision createDivision(String role, String email, StudentDivision division);
    StudentDivisionDTO getDivisionById(Long id, String role, String email);
    StudentDivision updateDivision(Long id,String role,String email,StudentDivision division);
    void deleteDivisionById(Long id,String role,String email);
    List<StudentDivisionDTO> getAllDivision(String role, String email, @Nullable String branchCodeFilter);
}
