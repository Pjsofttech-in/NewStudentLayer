package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.Entity.StudentAcademicYear;

import java.util.List;

public interface AcademicYearService
{

    StudentAcademicYear createAcademicYear(String role, String email, StudentAcademicYear academicYear);
    StudentAcademicYear getAcademicYearById(Long id,String role,String email);
    StudentAcademicYear updateAcademicYear(Long id,String role,String email,StudentAcademicYear academicYear);
    void deleteAcademicYearById(Long id,String role,String email);
    List<StudentAcademicYear> getAllAcademicYear(String role, String email);

}
