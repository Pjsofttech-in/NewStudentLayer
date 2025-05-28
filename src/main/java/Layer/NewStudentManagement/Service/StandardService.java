package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.Entity.StudentStandard;

import java.util.List;

public interface StandardService
{
    StudentStandard createStandard(String role,String email,StudentStandard standard);
    StudentStandard getStandardById(Long id,String role,String email);
    StudentStandard updateStandard(Long id,String role,String email,StudentStandard standard);
    void deleteStandardById(Long id,String role,String email);
    List<StudentStandard> getAllStandard(String role, String email);
}
