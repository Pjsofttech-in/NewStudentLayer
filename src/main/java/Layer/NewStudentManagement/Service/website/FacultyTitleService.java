package Layer.NewStudentManagement.Service.website;


import Layer.NewStudentManagement.Entity.website.StudentWebFacultyTitle;

import java.util.List;

public interface FacultyTitleService {
    StudentWebFacultyTitle createFacilityTitle(StudentWebFacultyTitle webFacultyTitle, String role, String email, String url);
    List<StudentWebFacultyTitle> getAllFacilityTitlesByBranchCode(String role, String email, String url, String branchCode);
    StudentWebFacultyTitle updateFacilityTitle(Long id, StudentWebFacultyTitle webFacultyTitle, String role, String email, String url);
    void deleteFacilityTitle(Long id, String role, String email, String url);
    StudentWebFacultyTitle getFacilityTitleById(Long id, String role, String email, String url);
}