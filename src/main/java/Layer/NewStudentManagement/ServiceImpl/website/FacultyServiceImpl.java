package Layer.NewStudentManagement.ServiceImpl.website;

import Layer.NewStudentManagement.Entity.website.StudentWebFaculty;
import Layer.NewStudentManagement.Entity.website.StudentWebSecurityUrl;
import Layer.NewStudentManagement.Exception.ResourceNotFoundException;
import Layer.NewStudentManagement.Repository.website.FacultyRepository;
import Layer.NewStudentManagement.Repository.website.SecurityUrlrepository;
import Layer.NewStudentManagement.Service.S3Service;
import Layer.NewStudentManagement.Service.website.FacultyService;
import Layer.NewStudentManagement.Service.website.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class FacultyServiceImpl implements FacultyService {

    @Autowired
    private FacultyRepository repository;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private SecurityUrlrepository securityUrlRepository;

    @Autowired
    private S3Service s3Service;

    private void validateUrlExists(String url, String branchCode) {

        String normalizedUrl = normalizeUrl(url);
        if (branchCode == null || branchCode.isBlank()) {
            securityUrlRepository.findByUrl(normalizedUrl)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Provided URL [" + url + "] does not exist"
                    ));
            return;
        }
        securityUrlRepository.findByUrlAndBranchCode(normalizedUrl, branchCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "URL [" + url + "] is not allowed for branchCode [" + branchCode + "]"
                ));
    }


    private String normalizeUrl(String url) {
        return (url == null) ? "" : url.split(",")[0].trim().toLowerCase();
    }

    @Override
    public StudentWebFaculty createFacility(StudentWebFaculty webFaculty, String role, String email, MultipartFile image, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create facility");
        }

        String branchCode = permissionService.fetchBranchCode(role, email);
        StudentWebSecurityUrl webSecurityUrl = securityUrlRepository.findByUrl(normalizeUrl(url))
                .orElseThrow(() -> new ResourceNotFoundException("Provided URL does not exist in security URL table"));

        // Apply static color logic
        List<StudentWebFaculty> existing = repository.findAll();
        if (!existing.isEmpty()) {
            webFaculty.setFacilityColor(existing.get(0).getFacilityColor());
        }

        webFaculty.setRole(role);
        webFaculty.setCreatedByEmail(email);
        webFaculty.setBranchCode(branchCode);
        webFaculty.setUrl(url);
        webFaculty.setWebSecurityUrl(webSecurityUrl);

        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = s3Service.uploadImage(image, branchCode);
                webFaculty.setFacilityImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload facility image", e);
            }
        }

        return repository.save(webFaculty);
    }


    @Override
    public List<StudentWebFaculty> getAllFacilitiesByBranchCode(String role, String email, String url, String branchCode) {
        validateUrlExists(url,branchCode);

        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view facilities by branch");
        }

        return repository.findAllByBranchCode(branchCode);
    }


    @Override
    public StudentWebFaculty updateFacility(Long id, StudentWebFaculty webFaculty, String role, String email, MultipartFile image, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update facility");
        }

        StudentWebFaculty existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found"));

        existing.setFacilityName(webFaculty.getFacilityName() != null ? webFaculty.getFacilityName() : existing.getFacilityName());
        existing.setSubject(webFaculty.getSubject() != null ? webFaculty.getSubject() : existing.getSubject());
        existing.setFacilityEducation(webFaculty.getFacilityEducation() != null ? webFaculty.getFacilityEducation() : existing.getFacilityEducation());
        existing.setDescription(webFaculty.getDescription() != null ? webFaculty.getDescription() : existing.getDescription());
        existing.setExperienceInYear(webFaculty.getExperienceInYear() != null ? webFaculty.getExperienceInYear() : existing.getExperienceInYear());

        // Static color update logic
        if (webFaculty.getFacilityColor() != null && !webFaculty.getFacilityColor().equals(existing.getFacilityColor())) {
            List<StudentWebFaculty> allFacilities = repository.findAll();
            for (StudentWebFaculty f : allFacilities) {
                f.setFacilityColor(webFaculty.getFacilityColor());
            }
            repository.saveAll(allFacilities);
        }

        String branchCode = permissionService.fetchBranchCode(role, email);

        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = s3Service.uploadImage(image, branchCode);
                if (existing.getFacilityImage() != null && existing.getFacilityImage().contains("amazonaws.com")) {
                    s3Service.deleteImage(existing.getFacilityImage());
                }
                existing.setFacilityImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload facility image", e);
            }
        }

        return repository.save(existing);
    }


    @Override
    public void deleteFacility(Long id, String role, String email, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete facility");
        }

        StudentWebFaculty webFaculty = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found"));

        if (webFaculty.getFacilityImage() != null && webFaculty.getFacilityImage().contains("amazonaws.com")) {
            s3Service.deleteImage(webFaculty.getFacilityImage());
        }

        repository.deleteById(id);
    }

    @Override
    public StudentWebFaculty getFacilityById(Long id, String role, String email, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view facility");
        }

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found"));
    }
}