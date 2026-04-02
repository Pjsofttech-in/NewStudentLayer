package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.Entity.StudentSchoolProfile;
import org.springframework.web.multipart.MultipartFile;

public interface SchoolProfileService
{
    StudentSchoolProfile createSchoolProfile(StudentSchoolProfile profile, MultipartFile logo,
                                             String role, String email);
    StudentSchoolProfile updateSchoolProfile(Long id, StudentSchoolProfile updatedProfile,
                                             MultipartFile logo, String role, String email);

    void deleteSchoolProfile(Long id, String role, String email);
    StudentSchoolProfile getSchoolProfileByBranchCode(String role, String email);

    StudentSchoolProfile getBySlug(String slug);

}
