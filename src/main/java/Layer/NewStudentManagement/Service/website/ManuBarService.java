package Layer.NewStudentManagement.Service.website;

import Layer.NewStudentManagement.Entity.website.StudentWebManuBar;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ManuBarService {
    StudentWebManuBar createManuBar(StudentWebManuBar webManuBar, String role, String email, MultipartFile menubarImage, String url);

    List<StudentWebManuBar> getAllByBranchCode(String role, String email, String url, String branchCode);

    StudentWebManuBar updateManuBar(Long id, StudentWebManuBar webManuBar, String role, String email, MultipartFile menubarImage, String url);

    void deleteManuBar(Long id, String role, String email, String url);

    StudentWebManuBar getManuBarById(Long id, String role, String email, String url);
}