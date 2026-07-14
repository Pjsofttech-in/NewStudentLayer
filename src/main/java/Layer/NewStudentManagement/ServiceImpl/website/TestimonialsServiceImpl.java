package Layer.NewStudentManagement.ServiceImpl.website;

import Layer.NewStudentManagement.Entity.website.StudentWebSecurityUrl;
import Layer.NewStudentManagement.Entity.website.StudentWebTestimonials;
import Layer.NewStudentManagement.Exception.ResourceNotFoundException;
import Layer.NewStudentManagement.Repository.website.SecurityUrlrepository;
import Layer.NewStudentManagement.Repository.website.TestimonialsRepository;
import Layer.NewStudentManagement.Service.S3Service;
import Layer.NewStudentManagement.Service.website.PermissionService;
import Layer.NewStudentManagement.Service.website.TestimonialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class TestimonialsServiceImpl implements TestimonialsService {

    @Autowired
    private TestimonialsRepository repository;

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
        if (url == null) return "";
        return url.split(",")[0].trim().toLowerCase();
    }

    @Override
    public StudentWebTestimonials create(StudentWebTestimonials webTestimonials, String role, String email, MultipartFile testimonialImage, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create testimonial");
        }

        String branchCode = permissionService.fetchBranchCode(role, email);
        StudentWebSecurityUrl webSecurityUrl = securityUrlRepository.findByUrl(normalizeUrl(url))
                .orElseThrow(() -> new ResourceNotFoundException("URL not found in SecurityUrl table"));

        // Static color logic
        List<StudentWebTestimonials> existingTestimonials = repository.findAll();
        if (!existingTestimonials.isEmpty()) {
            webTestimonials.setTestimonialColor(existingTestimonials.get(0).getTestimonialColor());
        }

        webTestimonials.setRole(role);
        webTestimonials.setCreatedByEmail(email);
        webTestimonials.setBranchCode(branchCode);
        webTestimonials.setUrl(url);
        webTestimonials.setWebSecurityUrl(webSecurityUrl);

        if (testimonialImage != null && !testimonialImage.isEmpty()) {
            try {
                String imageUrl = s3Service.uploadImage(testimonialImage, branchCode);
                webTestimonials.setTestimonialImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload testimonial image", e);
            }
        }

        return repository.save(webTestimonials);
    }

    @Override
    public List<StudentWebTestimonials> getAllByBranchCode(String role, String email, String branchCode, String url) {
        validateUrlExists(url,branchCode);

        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view testimonials by branch code");
        }

        return repository.findAllByBranchCode(branchCode);
    }


    @Override
    public StudentWebTestimonials update(Long id, StudentWebTestimonials webTestimonials, String role, String email, MultipartFile testimonialImage, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update testimonial");
        }

        StudentWebTestimonials existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Testimonial not found"));

        if (webTestimonials.getTestimonialTitle() != null)
            existing.setTestimonialTitle(webTestimonials.getTestimonialTitle());
        if (webTestimonials.getTestimonialName() != null)
            existing.setTestimonialName(webTestimonials.getTestimonialName());
        if (webTestimonials.getExam() != null)
            existing.setExam(webTestimonials.getExam());
        if (webTestimonials.getPost() != null)
            existing.setPost(webTestimonials.getPost());
        if (webTestimonials.getDescription() != null)
            existing.setDescription(webTestimonials.getDescription());

        // Static color logic
        if (webTestimonials.getTestimonialColor() != null &&
                !webTestimonials.getTestimonialColor().equals(existing.getTestimonialColor())) {
            List<StudentWebTestimonials> allTestimonials = repository.findAll();
            for (StudentWebTestimonials t : allTestimonials) {
                t.setTestimonialColor(webTestimonials.getTestimonialColor());
            }
            repository.saveAll(allTestimonials);
            existing.setTestimonialColor(webTestimonials.getTestimonialColor());
        }

        String branchCode = permissionService.fetchBranchCode(role, email);

        if (testimonialImage != null && !testimonialImage.isEmpty()) {
            try {
                String imageUrl = s3Service.uploadImage(testimonialImage, branchCode);
                if (existing.getTestimonialImage() != null && existing.getTestimonialImage().contains("amazonaws.com")) {
                    s3Service.deleteImage(existing.getTestimonialImage());
                }
                existing.setTestimonialImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload testimonial image", e);
            }
        }

        return repository.save(existing);
    }


    @Override
    public void delete(Long id, String role, String email, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete testimonial");
        }

        StudentWebTestimonials testimonial = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Testimonial not found"));

        if (testimonial.getTestimonialImage() != null && testimonial.getTestimonialImage().contains("amazonaws.com")) {
            s3Service.deleteImage(testimonial.getTestimonialImage());
        }

        repository.deleteById(id);
    }

    @Override
    public StudentWebTestimonials getById(Long id, String role, String email, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view testimonial");
        }

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Testimonial not found"));
    }
}
