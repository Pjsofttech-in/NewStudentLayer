package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentSchoolProfile;
import Layer.NewStudentManagement.Repository.SchoolProfileRepository;
import Layer.NewStudentManagement.Service.S3Service;
import Layer.NewStudentManagement.Service.SchoolProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SchoolProfileServiceImpl implements SchoolProfileService
{

    @Autowired
    SchoolProfileRepository schoolProfileRepository;

    @Autowired
    StaffService staffService;


    @Autowired
    S3Service s3Service;

    @Override
    public StudentSchoolProfile createSchoolProfile(StudentSchoolProfile profile, MultipartFile logo,
                                                    String role, String email) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to create School Profile");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        profile.setBranchCode(branchCode);

        if (schoolProfileRepository.existsByBranchCode(branchCode)) {
            throw new RuntimeException("School already exists for this branch");
        }

        if (logo != null && !logo.isEmpty()) {
            String uploadedUrl = s3Service.uploadFile(logo, branchCode);
            profile.setSchoolLogo(uploadedUrl);
        }
        profile.setCreatedByEmail(email);
        profile.setRole(role);

        return schoolProfileRepository.save(profile);
    }

    @Override
    public StudentSchoolProfile updateSchoolProfile(Long id, StudentSchoolProfile updatedProfile,
                                                    MultipartFile logo, String role, String email) {
        if (!staffService.hasPermission(role, email, "Put")) {
            throw new RuntimeException("You don't have permission to update School Profile");
        }

        StudentSchoolProfile existing = schoolProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("School Profile not found with ID: " + id));

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        if (!existing.getBranchCode().equals(branchCode)) {
            throw new RuntimeException("You are not allowed to update another branch's profile");
        }

        existing.setSchoolName(updatedProfile.getSchoolName());
        existing.setUdiseNumber(updatedProfile.getUdiseNumber());
        existing.setSchoolAddress(updatedProfile.getSchoolAddress());
        existing.setContactNumber(updatedProfile.getContactNumber());
        existing.setSchoolEmail(updatedProfile.getSchoolEmail());
        existing.setPlace(updatedProfile.getPlace());
        existing.setIndexNumber(updatedProfile.getIndexNumber());
        existing.setSocietyName(updatedProfile.getSocietyName());
        existing.setRole(role);
        existing.setCreatedByEmail(email);

        if (logo != null && !logo.isEmpty()) {
            String uploadedUrl = s3Service.uploadFile(logo, branchCode);
            existing.setSchoolLogo(uploadedUrl);
        }

        return schoolProfileRepository.save(existing);
    }

    @Override
    public void deleteSchoolProfile(Long id, String role, String email) {
        if (!staffService.hasPermission(role, email, "Delete")) {
            throw new RuntimeException("You don't have permission to delete School Profile");
        }

        StudentSchoolProfile profile = schoolProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("School Profile not found with ID: " + id));

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        if (!profile.getBranchCode().equals(branchCode)) {
            throw new RuntimeException("You are not allowed to delete another branch's profile");
        }

        schoolProfileRepository.delete(profile);
    }

    @Override
    public StudentSchoolProfile getSchoolProfileByBranchCode(String role, String email) {
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        return schoolProfileRepository.findByBranchCode(branchCode)
                .orElseThrow(() -> new RuntimeException("No School Profile found for branch: " + branchCode));
    }


}
