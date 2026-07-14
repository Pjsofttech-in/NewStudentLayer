package Layer.NewStudentManagement.ServiceImpl.website;

import Layer.NewStudentManagement.Entity.website.StudentWebCourse;
import Layer.NewStudentManagement.Entity.website.StudentWebSecurityUrl;
import Layer.NewStudentManagement.Exception.ResourceNotFoundException;
import Layer.NewStudentManagement.Repository.website.CourseRepository;
import Layer.NewStudentManagement.Repository.website.SecurityUrlrepository;
import Layer.NewStudentManagement.Service.S3Service;
import Layer.NewStudentManagement.Service.website.CourseService;
import Layer.NewStudentManagement.Service.website.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository repository;

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
    public StudentWebCourse createCourse(StudentWebCourse webCourse, String role, String email, MultipartFile courseImage, String url) {
        validateUrlExists(url, null);


        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create course");
        }

        String branchCode = permissionService.fetchBranchCode(role, email);
        StudentWebSecurityUrl webSecurityUrl = securityUrlRepository.findByUrl(normalizeUrl(url))
                .orElseThrow(() -> new ResourceNotFoundException("Provided URL does not exist in security URL table"));

        // ✅ If it's not the first course, apply color from the first course
        List<StudentWebCourse> allCours = repository.findAll();
        if (!allCours.isEmpty()) {
            webCourse.setCourseColor(allCours.get(0).getCourseColor());
        }
        // ✅ If it's the first course, use the color from the request (leave as-is)

        webCourse.setRole(role);
        webCourse.setCreatedByEmail(email);
        webCourse.setBranchCode(branchCode);
        webCourse.setUrl(url);
        webCourse.setWebSecurityUrl(webSecurityUrl);

        if (courseImage != null && !courseImage.isEmpty()) {
            try {
                String imageUrl = s3Service.uploadImage(courseImage, branchCode);
                webCourse.setCourseImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload course image", e);
            }
        }

        return repository.save(webCourse);
    }



    @Override
    public List<StudentWebCourse> getAllCoursesByBranchCode(String role, String email, String branchCode, String url) {
        validateUrlExists(url,branchCode);

        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view courses by branch code");
        }

        return repository.findAllByBranchCode(branchCode);
    }


    @Override
    public StudentWebCourse updateCourse(int id, StudentWebCourse webCourse, String role, String email, MultipartFile courseImage, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update course");
        }

        StudentWebCourse existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        existing.setCourseName(webCourse.getCourseName() != null ? webCourse.getCourseName() : existing.getCourseName());
        existing.setCourseDescription(webCourse.getCourseDescription() != null ? webCourse.getCourseDescription() : existing.getCourseDescription());
        existing.setUrl(webCourse.getUrl() != null ? webCourse.getUrl() : existing.getUrl());

        // If course color is updated, propagate to all courses
        String newColor = webCourse.getCourseColor();
        if (newColor != null && !newColor.equals(existing.getCourseColor())) {
            List<StudentWebCourse> allCours = repository.findAll();
            for (StudentWebCourse c : allCours) {
                c.setCourseColor(newColor);
            }
            repository.saveAll(allCours);
        }

        String branchCode = permissionService.fetchBranchCode(role, email);

        if (courseImage != null && !courseImage.isEmpty()) {
            try {
                String newImageUrl = s3Service.uploadImage(courseImage, branchCode);

                if (existing.getCourseImage() != null && existing.getCourseImage().contains("amazonaws.com")) {
                    s3Service.deleteImage(existing.getCourseImage());
                }

                existing.setCourseImage(newImageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload course image", e);
            }
        }

        return repository.save(existing);
    }


    @Override
    public void deleteCourse(int id, String role, String email, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete course");
        }

        StudentWebCourse webCourse = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (webCourse.getCourseImage() != null && webCourse.getCourseImage().contains("amazonaws.com")) {
            s3Service.deleteImage(webCourse.getCourseImage());
        }

        repository.deleteById(id);
    }

    @Override
    public StudentWebCourse getCourseById(int id, String role, String email, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view course");
        }

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }
}