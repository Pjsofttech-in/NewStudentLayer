package Layer.NewStudentManagement.ServiceImpl.website;

import Layer.NewStudentManagement.Entity.website.StudentWebSecurityUrl;
import Layer.NewStudentManagement.Entity.website.StudentWebTopper;
import Layer.NewStudentManagement.Exception.ResourceNotFoundException;
import Layer.NewStudentManagement.Repository.website.SecurityUrlrepository;
import Layer.NewStudentManagement.Repository.website.TopperRepository;
import Layer.NewStudentManagement.Service.S3Service;
import Layer.NewStudentManagement.Service.website.PermissionService;
import Layer.NewStudentManagement.Service.website.TopperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class TopperServiceImpl implements TopperService {

    @Autowired
    private TopperRepository repository;

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
    public StudentWebTopper createTopper(StudentWebTopper webTopper, String role, String email, MultipartFile topperImage, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create topper");
        }

        String branchCode = permissionService.fetchBranchCode(role, email);
        StudentWebSecurityUrl webSecurityUrl = securityUrlRepository.findByUrl(normalizeUrl(url))
                .orElseThrow(() -> new ResourceNotFoundException("Provided URL does not exist in security URL table"));

        // Apply static color from first record if exists
        List<StudentWebTopper> existingStudentWebToppers = repository.findAll();
        if (!existingStudentWebToppers.isEmpty()) {
            webTopper.setTopperColor(existingStudentWebToppers.get(0).getTopperColor());
        }

        webTopper.setRole(role);
        webTopper.setCreatedByEmail(email);
        webTopper.setBranchCode(branchCode);
        webTopper.setUrl(url);
        webTopper.setWebSecurityUrl(webSecurityUrl);

        if (topperImage != null && !topperImage.isEmpty()) {
            try {
                String imageUrl = s3Service.uploadImage(topperImage, branchCode);
                webTopper.setTopperImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload Topper image", e);
            }
        }

        return repository.save(webTopper);
    }


    @Override
    public List<StudentWebTopper> getAllToppersByBranchCode(String role, String email, String branchCode, String url) {
        validateUrlExists(url,branchCode);

        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view toppers by branch code");
        }

        return repository.findAllByBranchCode(branchCode);
    }

    @Override
    public StudentWebTopper updateTopper(Long id, StudentWebTopper updatedStudentWebTopper, String role, String email, MultipartFile topperImage, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update topper");
        }

        StudentWebTopper existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topper not found"));

        existing.setName(updatedStudentWebTopper.getName() != null ? updatedStudentWebTopper.getName() : existing.getName());
        existing.setPost(updatedStudentWebTopper.getPost() != null ? updatedStudentWebTopper.getPost() : existing.getPost());
        existing.setTotalMarks(updatedStudentWebTopper.getTotalMarks() != null ? updatedStudentWebTopper.getTotalMarks() : existing.getTotalMarks());
        existing.setRank(updatedStudentWebTopper.getRank() != null ? updatedStudentWebTopper.getRank() : existing.getRank());
        existing.setYear(updatedStudentWebTopper.getYear() != null ? updatedStudentWebTopper.getYear() : existing.getYear());
        existing.setTopperImages(updatedStudentWebTopper.getTopperImages() != null ? updatedStudentWebTopper.getTopperImages() : existing.getTopperImages());
        existing.setImageUrlIds(updatedStudentWebTopper.getImageUrlIds() != null ? updatedStudentWebTopper.getImageUrlIds() : existing.getImageUrlIds());

        String branchCode = permissionService.fetchBranchCode(role, email);

        // Update all if color is changed
        if (updatedStudentWebTopper.getTopperColor() != null && !updatedStudentWebTopper.getTopperColor().equals(existing.getTopperColor())) {
            List<StudentWebTopper> allStudentWebToppers = repository.findAll();
            for (StudentWebTopper webTopper : allStudentWebToppers) {
                webTopper.setTopperColor(updatedStudentWebTopper.getTopperColor());
            }
            repository.saveAll(allStudentWebToppers); // Save all updated
        }

        if (topperImage != null && !topperImage.isEmpty()) {
            try {
                String imageUrl = s3Service.uploadImage(topperImage, branchCode);

                if (existing.getTopperImage() != null && existing.getTopperImage().contains("amazonaws.com")) {
                    s3Service.deleteImage(existing.getTopperImage());
                }

                existing.setTopperImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload Topper image", e);
            }
        }

        return repository.save(existing);
    }

    @Override
    public void deleteTopper(Long id, String role, String email, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete topper");
        }

        StudentWebTopper webTopper = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topper not found"));

        if (webTopper.getTopperImage() != null && webTopper.getTopperImage().contains("amazonaws.com")) {
            s3Service.deleteImage(webTopper.getTopperImage());
        }

        repository.deleteById(id);
    }

    @Override
    public StudentWebTopper getTopperById(Long id, String role, String email, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view topper");
        }

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topper not found"));
    }
}