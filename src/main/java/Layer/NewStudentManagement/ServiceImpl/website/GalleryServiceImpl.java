package Layer.NewStudentManagement.ServiceImpl.website;

import Layer.NewStudentManagement.Entity.website.StudentWebGallery;
import Layer.NewStudentManagement.Entity.website.StudentWebSecurityUrl;
import Layer.NewStudentManagement.Exception.ResourceNotFoundException;
import Layer.NewStudentManagement.Repository.website.GalleryRepository;
import Layer.NewStudentManagement.Repository.website.SecurityUrlrepository;
import Layer.NewStudentManagement.Service.S3Service;
import Layer.NewStudentManagement.Service.website.GalleryService;
import Layer.NewStudentManagement.Service.website.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GalleryServiceImpl implements GalleryService {

    @Autowired
    private GalleryRepository repository;

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
    public StudentWebGallery createGallery(StudentWebGallery webGallery, String role, String email, List<MultipartFile> images, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "POST"))
            throw new AccessDeniedException("No permission to create gallery");

        String branchCode = permissionService.fetchBranchCode(role, email);

        StudentWebSecurityUrl webSecurityUrl = securityUrlRepository.findByUrl(normalizeUrl(url))
                .orElseThrow(() -> new ResourceNotFoundException("Security URL not found"));

        webGallery.setBranchCode(branchCode);
        webGallery.setCreatedByEmail(email);
        webGallery.setRole(role);
        webGallery.setUrl(url);
        webGallery.setWebSecurityUrl(webSecurityUrl);

        // Static color logic
        List<StudentWebGallery> existing = repository.findAll();
        if (!existing.isEmpty()) {
            webGallery.setGalleryColor(existing.get(0).getGalleryColor());
        }

        // Upload images to S3
        List<String> uploaded = new ArrayList<>();
        if (images != null) {
            for (MultipartFile image : images) {
                try {
                    String imageUrl = s3Service.uploadImage(image, branchCode);
                    uploaded.add(imageUrl);
                } catch (IOException e) {
                    throw new RuntimeException("Image upload failed", e);
                }
            }
        }
        webGallery.setGalleryImages(uploaded);

        return repository.save(webGallery);
    }


    @Override
    public List<StudentWebGallery> getAllGalleriesByBranchCode(String role, String email, String url, String branchCode) {
        validateUrlExists(url,branchCode);
        if (!permissionService.hasPermission(role, email, "GET"))
            throw new AccessDeniedException("No permission to view galleries by branch");

        return repository.findAllByBranchCode(branchCode);
    }


    @Override
    public StudentWebGallery updateGallery(Long id, StudentWebGallery webGallery, String role, String email,
                                    List<MultipartFile> newImages, List<String> deleteImages, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "PUT"))
            throw new AccessDeniedException("No permission");

        StudentWebGallery existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gallery not found"));

        if (webGallery.getEventName() != null) existing.setEventName(webGallery.getEventName());
        if (webGallery.getYear() != null) existing.setYear(webGallery.getYear());
        if (webGallery.getUrl() != null) existing.setUrl(webGallery.getUrl());

        // Static color update logic
        if (webGallery.getGalleryColor() != null && !webGallery.getGalleryColor().equals(existing.getGalleryColor())) {
            List<StudentWebGallery> all = repository.findAll();
            for (StudentWebGallery g : all) {
                g.setGalleryColor(webGallery.getGalleryColor());
            }
            repository.saveAll(all); // Update color for all galleries
            existing.setGalleryColor(webGallery.getGalleryColor()); // Optional: update again if not picked above
        }

        List<String> currentImages = new ArrayList<>(Optional.ofNullable(existing.getGalleryImages()).orElse(new ArrayList<>()));

        if (deleteImages != null) {
            currentImages.removeIf(imageUrl -> {
                String fileName = extractFileName(imageUrl);
                if (deleteImages.contains(fileName)) {
                    s3Service.deleteImage(imageUrl);
                    return true;
                }
                return false;
            });
        }

        if (newImages != null) {
            String branchCode = permissionService.fetchBranchCode(role, email);
            for (MultipartFile image : newImages) {
                try {
                    String imageUrl = s3Service.uploadImage(image, branchCode);
                    currentImages.add(imageUrl);
                } catch (IOException e) {
                    throw new RuntimeException("Image upload failed", e);
                }
            }
        }

        existing.setGalleryImages(currentImages);
        return repository.save(existing);
    }


    private String extractFileName(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }

    @Override
    public void deleteGallery(Long id, String role, String email, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "DELETE"))
            throw new AccessDeniedException("No permission");

        StudentWebGallery webGallery = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gallery not found"));

        if (webGallery.getGalleryImages() != null) {
            for (String image : webGallery.getGalleryImages()) {
                s3Service.deleteImage(image);
            }
        }

        repository.deleteById(id);
    }

    @Override
    public StudentWebGallery getGalleryById(Long id, String role, String email, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "GET"))
            throw new AccessDeniedException("No permission");

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gallery not found"));
    }

}