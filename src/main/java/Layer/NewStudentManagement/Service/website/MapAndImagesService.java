package Layer.NewStudentManagement.Service.website;

import Layer.NewStudentManagement.Entity.website.StudentWebMapAndImages;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MapAndImagesService {
    StudentWebMapAndImages create(StudentWebMapAndImages entity, String role, String email, MultipartFile imageFile, String url);
    List<StudentWebMapAndImages> getAllByBranchCode(String role, String email, String url, String branchCode);
    StudentWebMapAndImages update(Long id, StudentWebMapAndImages webMapAndImages, String role, String email, MultipartFile imageFile, String url);
    void delete(Long id, String role, String email, String url);
    StudentWebMapAndImages getById(Long id, String role, String email, String url);
}