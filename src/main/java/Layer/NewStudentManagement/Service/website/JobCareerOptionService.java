package Layer.NewStudentManagement.Service.website;

import Layer.NewStudentManagement.DTO.WebJobCareerOptionDTO;
import Layer.NewStudentManagement.Entity.website.StudentWebJobCareerOption;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JobCareerOptionService {
    WebJobCareerOptionDTO create(StudentWebJobCareerOption option, String role, String email, MultipartFile resumeFile, String url, Long webHRDetailsId);
    WebJobCareerOptionDTO update(Long id, StudentWebJobCareerOption option, String role, String email, MultipartFile resumeFile, String url, Long webHRDetailsId);
    List<WebJobCareerOptionDTO> getAllByBranchCode(String role, String email, String url, String branchCode);
    WebJobCareerOptionDTO getById(Long id, String role, String email, String url);
    void delete(Long id, String role, String email, String url);
}