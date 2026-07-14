package Layer.NewStudentManagement.ServiceImpl.website;

import Layer.NewStudentManagement.Entity.website.StudentWebManuBar;
import Layer.NewStudentManagement.Entity.website.StudentWebSecurityUrl;
import Layer.NewStudentManagement.Exception.AlreadyExistsException;
import Layer.NewStudentManagement.Exception.ResourceNotFoundException;
import Layer.NewStudentManagement.Repository.website.ManuBarRepository;
import Layer.NewStudentManagement.Repository.website.SecurityUrlrepository;
import Layer.NewStudentManagement.Service.S3Service;
import Layer.NewStudentManagement.Service.website.ManuBarService;
import Layer.NewStudentManagement.Service.website.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ManuBarServiceImpl implements ManuBarService {

    @Autowired
    private ManuBarRepository repository;

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
    public StudentWebManuBar createManuBar(StudentWebManuBar webManuBar, String role, String email, MultipartFile menubarImage, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create ManuBar");
        }

        String branchCode = permissionService.fetchBranchCode(role, email);

        // Prevent duplicate creation
        repository.findFirstByBranchCode(branchCode).ifPresent(existing -> {
            throw new AlreadyExistsException("Menu bar already exists for this branch");
        });

        StudentWebSecurityUrl webSecurityUrl = securityUrlRepository.findByUrl(normalizeUrl(url))
                .orElseThrow(() -> new ResourceNotFoundException("Provided URL does not exist in security URL table"));

        webManuBar.setRole(role);
        webManuBar.setCreatedByEmail(email);
        webManuBar.setBranchCode(branchCode);
        webManuBar.setUrl(url);
        webManuBar.setWebSecurityUrl(webSecurityUrl);

        if (menubarImage != null && !menubarImage.isEmpty()) {
            try {
                String imageUrl = s3Service.uploadImage(menubarImage, branchCode);
                webManuBar.setMenubarImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload ManuBar image", e);
            }
        }

        return repository.save(webManuBar);
    }



    @Override
    public List<StudentWebManuBar> getAllByBranchCode(String role, String email, String url, String branchCode) {
        validateUrlExists(url,branchCode);

        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view ManuBars");
        }

        return repository.findAllByBranchCode(branchCode);
    }


    @Override
    public StudentWebManuBar updateManuBar(Long id, StudentWebManuBar webManuBar, String role, String email, MultipartFile menubarImage, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update ManuBar");
        }

        StudentWebManuBar existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ManuBar not found"));

        existing.setManuBarColor(webManuBar.getManuBarColor() != null ? webManuBar.getManuBarColor() : existing.getManuBarColor());
        existing.setUrl(webManuBar.getUrl() != null ? webManuBar.getUrl() : existing.getUrl());
        existing.setMenubarName(webManuBar.getMenubarName()!=null ? webManuBar.getMenubarName():existing.getMenubarName());

        String branchCode = permissionService.fetchBranchCode(role, email);

        if (menubarImage != null && !menubarImage.isEmpty()) {
            try {
                String imageUrl = s3Service.uploadImage(menubarImage, branchCode);

                if (existing.getMenubarImage() != null && existing.getMenubarImage().contains("amazonaws.com")) {
                    s3Service.deleteImage(existing.getMenubarImage());
                }

                existing.setMenubarImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload ManuBar image", e);
            }
        }

        return repository.save(existing);
    }

    @Override
    public void deleteManuBar(Long id, String role, String email, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete ManuBar");
        }

        StudentWebManuBar webManuBar = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ManuBar not found"));

        if (webManuBar.getMenubarImage() != null && webManuBar.getMenubarImage().contains("amazonaws.com")) {
            s3Service.deleteImage(webManuBar.getMenubarImage());
        }

        repository.deleteById(id);
    }

    @Override
    public StudentWebManuBar getManuBarById(Long id, String role, String email, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view ManuBar");
        }

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ManuBar not found"));
    }
}