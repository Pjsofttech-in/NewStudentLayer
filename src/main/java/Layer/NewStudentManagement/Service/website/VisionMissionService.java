package Layer.NewStudentManagement.Service.website;

import Layer.NewStudentManagement.Entity.website.StudentWebVisionMission;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VisionMissionService {
    StudentWebVisionMission create(StudentWebVisionMission vm, String role, String email, MultipartFile directorImage, String url);
    List<StudentWebVisionMission> getAllByBranchCode(String role, String email, String branchCode, String url);
    StudentWebVisionMission update(Long id, StudentWebVisionMission vm, String role, String email, MultipartFile directorImage, String url);
    void delete(Long id, String role, String email, String url);
    StudentWebVisionMission getById(Long id, String role, String email, String url);
}