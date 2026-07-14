package Layer.NewStudentManagement.ServiceImpl.website;

import Layer.NewStudentManagement.Entity.website.StudentWebSecurityUrl;
import Layer.NewStudentManagement.Entity.website.StudentWebSlideBar;
import Layer.NewStudentManagement.Exception.AlreadyExistsException;
import Layer.NewStudentManagement.Exception.ResourceNotFoundException;
import Layer.NewStudentManagement.Repository.website.SecurityUrlrepository;
import Layer.NewStudentManagement.Repository.website.SlideBarRepository;
import Layer.NewStudentManagement.Service.S3Service;
import Layer.NewStudentManagement.Service.website.PermissionService;
import Layer.NewStudentManagement.Service.website.SlideBarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class SlideBarServiceImpl implements SlideBarService {

    @Autowired
    private SlideBarRepository repository;

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
    public StudentWebSlideBar createSlideBar(StudentWebSlideBar webSlideBar, String role, String email, List<MultipartFile> slideBarImages, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "POST"))
            throw new AccessDeniedException("No permission to create slide bar");

        String branchCode = permissionService.fetchBranchCode(role, email);

        // Prevent duplicate SlideBar per branch
        repository.findFirstByBranchCode(branchCode).ifPresent(existing -> {
            throw new AlreadyExistsException("SlideBar already exists for this branch");
        });

        StudentWebSecurityUrl webSecurityUrl = securityUrlRepository.findByUrl(normalizeUrl(url))
                .orElseThrow(() -> new ResourceNotFoundException("Provided URL does not exist in security URL table"));

        webSlideBar.setBranchCode(branchCode);
        webSlideBar.setCreatedByEmail(email);
        webSlideBar.setRole(role);
        webSlideBar.setUrl(url);
        webSlideBar.setWebSecurityUrl(webSecurityUrl);

        List<String> uploadedUrls = new ArrayList<>();
        if (slideBarImages != null && !slideBarImages.isEmpty()) {
            for (MultipartFile imageFile : slideBarImages) {
                if (!imageFile.isEmpty()) {
                    try {
                        String imageUrl = s3Service.uploadImage(imageFile, branchCode);
                        uploadedUrls.add(imageUrl);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to upload slide bar image", e);
                    }
                }
            }
        }

        webSlideBar.setSlideImages(uploadedUrls); // ✅ ensure it's a mutable list

        return repository.save(webSlideBar);
    }




    @Override
    public List<StudentWebSlideBar> getAllByBranchCode(String role, String email, String branchCode, String url) {
        validateUrlExists(url,branchCode);

        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to get SlideBar by branch code");
        }

        return repository.findAllByBranchCode(branchCode);
    }



    @Override
    public StudentWebSlideBar updateSlideBar(Long id, StudentWebSlideBar webSlideBar, String role, String email,
                                      List<MultipartFile> newImages, List<String> deleteImages, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "PUT"))
            throw new AccessDeniedException("No permission to update slide bar");

        StudentWebSlideBar existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SlideBar not found"));

        // Update optional fields
        if (webSlideBar != null) {
            if (webSlideBar.getSlideBarColor() != null) {
                existing.setSlideBarColor(webSlideBar.getSlideBarColor());
            }
            if (webSlideBar.getUrl() != null) {
                existing.setUrl(webSlideBar.getUrl());
            }

        }

        // Make sure current slide images list is mutable
        List<String> currentImages = existing.getSlideImages() != null
                ? new ArrayList<>(existing.getSlideImages())
                : new ArrayList<>();

        // Delete matching images by filename
        if (!currentImages.isEmpty() && deleteImages != null && !deleteImages.isEmpty()) {
            Iterator<String> iterator = currentImages.iterator();
            while (iterator.hasNext()) {
                String existingImageUrl = iterator.next();
                String existingImageName = extractFileName(existingImageUrl);
                if (deleteImages.contains(existingImageName)) {
                    if (existingImageUrl.contains("amazonaws.com")) {
                        s3Service.deleteImage(existingImageUrl);
                    }
                    iterator.remove();
                }
            }
            existing.setSlideImages(currentImages);
        }

        // Upload and add new images
        if (newImages != null && !newImages.isEmpty()) {
            String branchCode = permissionService.fetchBranchCode(role, email);
            for (MultipartFile imageFile : newImages) {
                if (!imageFile.isEmpty()) {
                    try {
                        String imageUrl = s3Service.uploadImage(imageFile, branchCode);
                        currentImages.add(imageUrl);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to upload slide bar image", e);
                    }
                }
            }
            existing.setSlideImages(currentImages); // update list after addition
        }

        return repository.save(existing);
    }

    private String extractFileName(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return "";
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }



    @Override
    public void deleteSlideBar(Long id, String role, String email, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "DELETE"))
            throw new AccessDeniedException("No permission to delete slide bar");

        StudentWebSlideBar webSlideBar = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SlideBar not found"));

        // ✅ Delete all images
        if (webSlideBar.getSlideImages() != null) {
            for (String image : webSlideBar.getSlideImages()) {
                if (image != null && image.contains("amazonaws.com")) {
                    s3Service.deleteImage(image);
                }
            }
        }

        repository.deleteById(id);
    }


    @Override
    public StudentWebSlideBar getSlideBarById(Long id, String role, String email, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "GET"))
            throw new AccessDeniedException("No permission to view slide bar");

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SlideBar not found"));
    }
}