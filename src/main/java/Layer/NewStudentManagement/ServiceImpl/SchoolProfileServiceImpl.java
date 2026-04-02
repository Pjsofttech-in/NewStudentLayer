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
    public StudentSchoolProfile createSchoolProfile(StudentSchoolProfile profile,
                                                    MultipartFile logo,
                                                    String role,
                                                    String email) {

        // ✅ 1. Permission check
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to create School Profile");
        }

        // ✅ 2. Fetch branchCode
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        profile.setBranchCode(branchCode);

        // ✅ 3. One profile per branch
        if (schoolProfileRepository.existsByBranchCode(branchCode)) {
            throw new RuntimeException("School already exists for this branch");
        }

        // ✅ 4. Upload logo (if present)
        if (logo != null && !logo.isEmpty()) {
            String uploadedUrl = s3Service.uploadFile(logo, branchCode);
            profile.setSchoolLogo(uploadedUrl);
        }

        // ✅ 5. SLUG LOGIC 🔥 (User input OR auto-generate)
        String slug;

        if (profile.getSchoolSlug() != null && !profile.getSchoolSlug().isEmpty()) {

            // ✔ Clean user input slug
            slug = profile.getSchoolSlug()
                    .toLowerCase()
                    .trim()
                    .replaceAll("[^a-z0-9-]", "")   // allow only a-z, 0-9, -
                    .replaceAll("-+", "-");         // remove duplicate hyphens

        } else {

            // ✔ Generate from school name
            slug = profile.getSchoolName()
                    .toLowerCase()
                    .trim()
                    .replaceAll("[^a-z0-9 ]", "")   // remove special chars
                    .replaceAll("\\s+", "-");       // space → hyphen
        }

        // ✅ 6. Ensure UNIQUE slug
        String baseSlug = slug;
        int count = 1;

        while (schoolProfileRepository.findBySchoolSlug(slug).isPresent()) {
            slug = baseSlug + "-" + count++;
        }

        profile.setSchoolSlug(slug);

        // ✅ 7. Set metadata
        profile.setCreatedByEmail(email);
        profile.setRole(role);

        // ✅ 8. Save
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
        existing.setBoard(updatedProfile.getBoard());
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


    @Override
    public StudentSchoolProfile getBySlug(String slug) {
        return schoolProfileRepository.findBySchoolSlug(slug.toLowerCase())
                .orElseThrow(() -> new RuntimeException("School not found"));
    }


}
