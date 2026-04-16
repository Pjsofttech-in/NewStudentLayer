package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.SchoolProfileDTO;
import Layer.NewStudentManagement.Entity.StudentSchoolProfile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SchoolProfileService
{
    SchoolProfileDTO createSchoolProfile(StudentSchoolProfile profile,
                                         MultipartFile logo,
                                         List<MultipartFile> images,
                                         String role,
                                         String email);

    SchoolProfileDTO updateSchoolProfile(Long id,
                                         StudentSchoolProfile profile,
                                         MultipartFile logo,
                                         List<MultipartFile> images,
                                         String role,
                                         String email);

    void deleteSchoolProfile(Long id, String role, String email);

    SchoolProfileDTO getSchoolProfileByBranchCode(String role, String email);

    SchoolProfileDTO getBySlug(String slug);

    void deleteImage(Long imageId);

}
