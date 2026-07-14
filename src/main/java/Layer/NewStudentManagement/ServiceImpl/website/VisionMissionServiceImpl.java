package Layer.NewStudentManagement.ServiceImpl.website;

import Layer.NewStudentManagement.Entity.website.StudentWebSecurityUrl;
import Layer.NewStudentManagement.Entity.website.StudentWebVisionMission;
import Layer.NewStudentManagement.Exception.AlreadyExistsException;
import Layer.NewStudentManagement.Exception.ResourceNotFoundException;
import Layer.NewStudentManagement.Repository.website.SecurityUrlrepository;
import Layer.NewStudentManagement.Repository.website.VisionMissionRepository;
import Layer.NewStudentManagement.Service.S3Service;
import Layer.NewStudentManagement.Service.website.PermissionService;
import Layer.NewStudentManagement.Service.website.VisionMissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class VisionMissionServiceImpl implements VisionMissionService {

    @Autowired
    private VisionMissionRepository repository;

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
    public StudentWebVisionMission create(StudentWebVisionMission vm, String role, String email, MultipartFile directorImage, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create vision/mission");
        }

        String branchCode = permissionService.fetchBranchCode(role, email);

        // ❗ Prevent duplicate creation per branch
        repository.findFirstByBranchCode(branchCode).ifPresent(existing -> {
            throw new AlreadyExistsException("Vision/Mission already exists for this branch");
        });

        StudentWebSecurityUrl webSecurityUrl = securityUrlRepository.findByUrl(normalizeUrl(url))
                .orElseThrow(() -> new ResourceNotFoundException("Provided URL does not exist in security URL table"));

        vm.setCreatedByEmail(email);
        vm.setRole(role);
        vm.setBranchCode(branchCode);
        vm.setUrl(url);
        vm.setWebSecurityUrl(webSecurityUrl);

        if (directorImage != null && !directorImage.isEmpty()) {
            try {
                String imageUrl = s3Service.uploadImage(directorImage, branchCode);
                vm.setDirectorImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload director image", e);
            }
        }

        return repository.save(vm);
    }


    @Override
    public List<StudentWebVisionMission> getAllByBranchCode(String role, String email, String branchCode, String url) {
        validateUrlExists(url,branchCode);

        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view vision/mission by branch code");
        }

        return repository.findAllByBranchCode(branchCode);
    }


    @Override
    public StudentWebVisionMission update(Long id, StudentWebVisionMission vm, String role, String email, MultipartFile directorImage, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update vision/mission");
        }

        StudentWebVisionMission existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VisionMission not found"));

        existing.setVision(vm.getVision() != null ? vm.getVision() : existing.getVision());
        existing.setMission(vm.getMission() != null ? vm.getMission() : existing.getMission());
        existing.setDirectorMessage(vm.getDirectorMessage() != null ? vm.getDirectorMessage() : existing.getDirectorMessage());
        existing.setDirectorName(vm.getDirectorName() != null ? vm.getDirectorName() : existing.getDirectorName());
        existing.setDescription(vm.getDescription() != null ? vm.getDescription() : existing.getDescription());
        existing.setVisionmissionColor(vm.getVisionmissionColor() != null ? vm.getVisionmissionColor() : existing.getVisionmissionColor());
        existing.setUrl(vm.getUrl() != null ? vm.getUrl() : existing.getUrl());

        String branchCode = permissionService.fetchBranchCode(role, email);

        if (directorImage != null && !directorImage.isEmpty()) {
            try {
                // Upload new image
                String imageUrl = s3Service.uploadImage(directorImage, branchCode);

                // Delete old image if exists and was uploaded to S3
                if (existing.getDirectorImage() != null && existing.getDirectorImage().contains("amazonaws.com")) {
                    s3Service.deleteImage(existing.getDirectorImage());
                }

                existing.setDirectorImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload director image", e);
            }
        }

        return repository.save(existing);
    }

    @Override
    public void delete(Long id, String role, String email, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete vision/mission");
        }

        StudentWebVisionMission vm = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VisionMission not found"));

        // Delete image from S3 if exists
        if (vm.getDirectorImage() != null && vm.getDirectorImage().contains("amazonaws.com")) {
            s3Service.deleteImage(vm.getDirectorImage());
        }

        repository.deleteById(id);
    }

    @Override
    public StudentWebVisionMission getById(Long id, String role, String email, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view vision/mission");
        }

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VisionMission not found"));
    }
}
