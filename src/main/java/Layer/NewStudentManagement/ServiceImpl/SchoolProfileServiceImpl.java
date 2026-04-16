package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.SchoolProfileDTO;
import Layer.NewStudentManagement.Entity.StudentProfileImage;
import Layer.NewStudentManagement.Entity.StudentSchoolProfile;

import Layer.NewStudentManagement.Repository.ProfileImageRepository;
import Layer.NewStudentManagement.Repository.SchoolProfileRepository;
import Layer.NewStudentManagement.Service.S3Service;
import Layer.NewStudentManagement.Service.SchoolProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SchoolProfileServiceImpl implements SchoolProfileService
{

    @Autowired
    SchoolProfileRepository schoolProfileRepository;

    @Autowired
    StaffService staffService;


    @Autowired
    S3Service s3Service;

    @Autowired
    private ProfileImageRepository profileImageRepository;

    @Override
    public SchoolProfileDTO createSchoolProfile(StudentSchoolProfile profile,
                                                MultipartFile logo,
                                                List<MultipartFile> images,
                                                String role,
                                                String email) {

        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("No permission");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        profile.setBranchCode(branchCode);

        if (schoolProfileRepository.existsByBranchCode(branchCode)) {
            throw new RuntimeException("School already exists");
        }

        // Logo upload
        if (logo != null && !logo.isEmpty()) {
            profile.setSchoolLogo(s3Service.uploadFile(logo, branchCode));
        }

        // SLUG
        String slug = (profile.getSchoolSlug() != null && !profile.getSchoolSlug().isEmpty())
                ? profile.getSchoolSlug()
                : profile.getSchoolName();

        slug = slug.toLowerCase().trim()
                .replaceAll("[^a-z0-9 ]", "")
                .replaceAll("\\s+", "-");

        String baseSlug = slug;
        int count = 1;

        while (schoolProfileRepository.findBySchoolSlug(slug).isPresent()) {
            slug = baseSlug + "-" + count++;
        }

        profile.setSchoolSlug(slug);
        profile.setCreatedByEmail(email);
        profile.setRole(role);

        // ✅ SAVE PROFILE ONCE
        StudentSchoolProfile savedProfile = schoolProfileRepository.save(profile);

        // ✅ SAVE IMAGES
        if (images != null && !images.isEmpty()) {
            List<StudentProfileImage> imageList = new ArrayList<>();

            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String url = s3Service.uploadFile(file, branchCode);

                    StudentProfileImage img = new StudentProfileImage();
                    img.setImageUrl(url);
                    img.setProfile(savedProfile);

                    imageList.add(img);
                }
            }

            profileImageRepository.saveAll(imageList);
            savedProfile.setImages(imageList);
        }

        return mapToDTO(savedProfile);
    }

    // ✅ UPDATE
    @Override
    public SchoolProfileDTO updateSchoolProfile(Long id,
                                                StudentSchoolProfile updatedProfile,
                                                MultipartFile logo,
                                                List<MultipartFile> images,
                                                String role,
                                                String email) {

        if (!staffService.hasPermission(role, email, "Put")) {
            throw new RuntimeException("No permission");
        }

        StudentSchoolProfile existing = schoolProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        existing.setSchoolName(updatedProfile.getSchoolName());
        existing.setSchoolAddress(updatedProfile.getSchoolAddress());
        existing.setContactNumber(updatedProfile.getContactNumber());
        existing.setSchoolEmail(updatedProfile.getSchoolEmail());
        existing.setPlace(updatedProfile.getPlace());
        existing.setSocietyName(updatedProfile.getSocietyName());
        existing.setIndexNumber(updatedProfile.getIndexNumber());
        existing.setBoard(updatedProfile.getBoard());

        if (logo != null && !logo.isEmpty()) {
            existing.setSchoolLogo(s3Service.uploadFile(logo, existing.getBranchCode()));
        }

        // ✅ ADD NEW IMAGES
        if (images != null && !images.isEmpty()) {
            List<StudentProfileImage> imageList = new ArrayList<>();

            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String url = s3Service.uploadFile(file, existing.getBranchCode());

                    StudentProfileImage img = new StudentProfileImage();
                    img.setImageUrl(url);
                    img.setProfile(existing);

                    imageList.add(img);
                }
            }

            profileImageRepository.saveAll(imageList);
        }

        StudentSchoolProfile saved = schoolProfileRepository.save(existing);
        return mapToDTO(saved);
    }

    // ✅ DELETE PROFILE
    @Override
    public void deleteSchoolProfile(Long id, String role, String email) {
        if (!staffService.hasPermission(role, email, "Delete")) {
            throw new RuntimeException("No permission");
        }

        StudentSchoolProfile profile = schoolProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        schoolProfileRepository.delete(profile);
    }

    // ✅ GET BY BRANCH
    @Override
    public SchoolProfileDTO getSchoolProfileByBranchCode(String role, String email) {
        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        StudentSchoolProfile profile = schoolProfileRepository.findByBranchCode(branchCode)
                .orElseThrow(() -> new RuntimeException("Not found"));

        return mapToDTO(profile);
    }

    // ✅ GET BY SLUG
    @Override
    public SchoolProfileDTO getBySlug(String slug) {
        StudentSchoolProfile profile = schoolProfileRepository
                .findBySchoolSlug(slug.toLowerCase())
                .orElseThrow(() -> new RuntimeException("Not found"));

        return mapToDTO(profile);
    }

    // ✅ DELETE IMAGE
    @Override
    public void deleteImage(Long imageId) {
        profileImageRepository.deleteById(imageId);
    }

    // ✅ DTO MAPPER
    private SchoolProfileDTO mapToDTO(StudentSchoolProfile profile) {

        SchoolProfileDTO dto = new SchoolProfileDTO();

        dto.setId(profile.getId());
        dto.setSchoolName(profile.getSchoolName());
        dto.setSchoolLogo(profile.getSchoolLogo());
        dto.setSchoolAddress(profile.getSchoolAddress());
        dto.setContactNumber(profile.getContactNumber());
        dto.setSchoolEmail(profile.getSchoolEmail());
        dto.setPlace(profile.getPlace());
        dto.setSocietyName(profile.getSocietyName());
        dto.setIndexNumber(profile.getIndexNumber());
        dto.setBoard(profile.getBoard());
        dto.setSchoolSlug(profile.getSchoolSlug());

        if (profile.getImages() != null) {
            List<String> imageUrls = profile.getImages()
                    .stream()
                    .map(StudentProfileImage::getImageUrl)
                    .toList();

            dto.setImages(imageUrls);
        }

        return dto;
    }
}
