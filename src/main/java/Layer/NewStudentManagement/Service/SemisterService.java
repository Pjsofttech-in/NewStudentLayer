package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.Entity.StudentSemister;

import java.util.List;

public interface SemisterService
{
    StudentSemister createSemister(String role, String email,StudentSemister semister);
    StudentSemister updateSemister(Long id,String role, String email,StudentSemister semister);
    List<StudentSemister> getAllSemister(String role, String email);
    StudentSemister getSemister(Long id,String role, String email);
    void deleteSemister(Long id,String role, String email);
}
