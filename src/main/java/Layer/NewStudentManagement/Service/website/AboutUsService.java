package Layer.NewStudentManagement.Service.website;

import Layer.NewStudentManagement.Entity.website.StudentWebAboutUs;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AboutUsService {
    public StudentWebAboutUs createAboutUs(StudentWebAboutUs webAboutUs, String role, String email, MultipartFile aboutUsImage, String url);
    List<StudentWebAboutUs> getAllAboutUsByBranchCode(String role, String email, String url, String branchCode);
    StudentWebAboutUs updateAboutUs(int id, StudentWebAboutUs webAboutUs, String role, String email, MultipartFile aboutUsImage, String url);
    void deleteAboutUs(int id, String role, String email, String url);
    StudentWebAboutUs getAboutUsById(int id, String role, String email, String url);
}
