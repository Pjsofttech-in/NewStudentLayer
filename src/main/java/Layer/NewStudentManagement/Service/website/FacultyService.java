package Layer.NewStudentManagement.Service.website;

import Layer.NewStudentManagement.Entity.website.StudentWebFaculty;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FacultyService {
    StudentWebFaculty createFacility(StudentWebFaculty webFaculty, String role, String email, MultipartFile image, String url);
    List<StudentWebFaculty> getAllFacilitiesByBranchCode(String role, String email, String url, String branchCode);
    StudentWebFaculty updateFacility(Long id, StudentWebFaculty webFaculty, String role, String email, MultipartFile image, String url);
    void deleteFacility(Long id, String role, String email, String url);
    StudentWebFaculty getFacilityById(Long id, String role, String email, String url);
}

