package Layer.NewStudentManagement.ServiceImpl.website;

import Layer.NewStudentManagement.Entity.website.StudentWebMapAndImages;
import Layer.NewStudentManagement.Entity.website.StudentWebSecurityUrl;
import Layer.NewStudentManagement.Exception.AlreadyExistsException;
import Layer.NewStudentManagement.Exception.ResourceNotFoundException;
import Layer.NewStudentManagement.Repository.website.MapAndImagesRepository;
import Layer.NewStudentManagement.Repository.website.SecurityUrlrepository;
import Layer.NewStudentManagement.Service.S3Service;
import Layer.NewStudentManagement.Service.website.MapAndImagesService;
import Layer.NewStudentManagement.Service.website.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class MapAndImagesServiceImpl implements MapAndImagesService {

    @Autowired
    private MapAndImagesRepository repository;

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
    public StudentWebMapAndImages create(StudentWebMapAndImages entity, String role, String email, MultipartFile imageFile, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create MapAndImages");
        }

        String branchCode = permissionService.fetchBranchCode(role, email);

        //   Prevent duplicate creation for the same branch
        repository.findFirstByBranchCode(branchCode).ifPresent(existing -> {
            throw new AlreadyExistsException("Map and Image already exists for this branch");
        });

        StudentWebSecurityUrl webSecurityUrl = securityUrlRepository.findByUrl(normalizeUrl(url))
                .orElseThrow(() -> new ResourceNotFoundException("Provided URL does not exist in security URL table"));

        entity.setRole(role);
        entity.setCreatedByEmail(email);
        entity.setBranchCode(branchCode);
        entity.setUrl(url);
        entity.setWebSecurityUrl(webSecurityUrl);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = s3Service.uploadImage(imageFile, branchCode);
                entity.setContactImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload contact image", e);
            }
        }

        return repository.save(entity);
    }


    @Override
    public List<StudentWebMapAndImages> getAllByBranchCode(String role, String email, String url, String branchCode) {
        validateUrlExists(url,branchCode);

        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view MapAndImages");
        }

        return repository.findAllByBranchCode(branchCode);
    }


    @Override
    public StudentWebMapAndImages update(Long id, StudentWebMapAndImages updated, String role, String email, MultipartFile imageFile, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update MapAndImages");
        }

        StudentWebMapAndImages existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MapAndImages not found"));

        existing.setMaps(updated.getMaps() != null ? updated.getMaps() : existing.getMaps());
        existing.setUrl(updated.getUrl() != null ? updated.getUrl() : existing.getUrl());

        String branchCode = permissionService.fetchBranchCode(role, email);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String newImageUrl = s3Service.uploadImage(imageFile, branchCode);

                // Optional: delete old image if stored in S3
                if (existing.getContactImage() != null && existing.getContactImage().contains("amazonaws.com")) {
                    s3Service.deleteImage(existing.getContactImage());
                }

                existing.setContactImage(newImageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload contact image", e);
            }
        }

        return repository.save(existing);
    }

    @Override
    public void delete(Long id, String role, String email, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete MapAndImages");
        }

        StudentWebMapAndImages entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MapAndImages not found"));

        if (entity.getContactImage() != null && entity.getContactImage().contains("amazonaws.com")) {
            s3Service.deleteImage(entity.getContactImage());
        }

        repository.deleteById(id);
    }

    @Override
    public StudentWebMapAndImages getById(Long id, String role, String email, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view MapAndImages");
        }

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MapAndImages not found"));
    }
}