package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.Entity.StudentDivision;

import java.util.List;

public interface DivisionService
{
    StudentDivision createDivision(String role, String email, StudentDivision division);
    StudentDivision getDivisionById(Long id,String role,String email);
    StudentDivision updateDivision(Long id,String role,String email,StudentDivision division);
    void deleteDivisionById(Long id,String role,String email);
    List<StudentDivision> getAllDivision(String role, String email);
}
