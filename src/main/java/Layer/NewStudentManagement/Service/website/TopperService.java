package Layer.NewStudentManagement.Service.website;

import Layer.NewStudentManagement.Entity.website.StudentWebTopper;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TopperService {
    StudentWebTopper createTopper(StudentWebTopper webTopper, String role, String email, MultipartFile topperImage, String url);
    List<StudentWebTopper> getAllToppersByBranchCode(String role, String email, String branchCode, String url);
    StudentWebTopper updateTopper(Long id, StudentWebTopper updatedStudentWebTopper, String role, String email, MultipartFile topperImage, String url);
    void deleteTopper(Long id, String role, String email, String url);
    StudentWebTopper getTopperById(Long id, String role, String email, String url);
}
