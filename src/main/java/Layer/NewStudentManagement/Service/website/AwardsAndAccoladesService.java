package Layer.NewStudentManagement.Service.website;

import Layer.NewStudentManagement.Entity.website.StudentWebAwardsAndAccolades;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AwardsAndAccoladesService {
    StudentWebAwardsAndAccolades createAward(StudentWebAwardsAndAccolades award, String role, String email, MultipartFile awardImage, String url);
    List<StudentWebAwardsAndAccolades> getAllAwardsByBranchCode(String role, String email, String url, String branchCode);
    StudentWebAwardsAndAccolades updateAward(Long id, StudentWebAwardsAndAccolades award, String role, String email, MultipartFile awardImage, String url);
    void deleteAward(Long id, String role, String email, String url);
    StudentWebAwardsAndAccolades getAwardById(Long id, String role, String email, String url);
}
