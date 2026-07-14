package Layer.NewStudentManagement.Service.website;
import Layer.NewStudentManagement.Entity.website.StudentWebGallery;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GalleryService {
    StudentWebGallery createGallery(StudentWebGallery webGallery, String role, String email, List<MultipartFile> images, String url);
    List<StudentWebGallery> getAllGalleriesByBranchCode(String role, String email, String url, String branchCode);
    StudentWebGallery updateGallery(Long id, StudentWebGallery webGallery, String role, String email,
                             List<MultipartFile> newImages, List<String> deleteImages, String url);
    void deleteGallery(Long id, String role, String email, String url);
    StudentWebGallery getGalleryById(Long id, String role, String email, String url);
}