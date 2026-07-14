package Layer.NewStudentManagement.Service.website;

import Layer.NewStudentManagement.Entity.website.StudentWebSlideBar;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SlideBarService {
    StudentWebSlideBar createSlideBar(StudentWebSlideBar webSlideBar, String role, String email, List<MultipartFile> slideBarImages, String url);
    List<StudentWebSlideBar> getAllByBranchCode(String role, String email, String branchCode, String url);
    StudentWebSlideBar updateSlideBar(Long id, StudentWebSlideBar webSlideBar, String role, String email,
                               List<MultipartFile> newImages, List<String> deleteImages, String url);

    void deleteSlideBar(Long id, String role, String email, String url);
    StudentWebSlideBar getSlideBarById(Long id, String role, String email, String url);
}