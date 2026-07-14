package Layer.NewStudentManagement.Service.website;

import Layer.NewStudentManagement.Entity.website.StudentWebTestimonials;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TestimonialsService {
    StudentWebTestimonials create(StudentWebTestimonials webTestimonials, String role, String email, MultipartFile testimonialImage, String url);
    List<StudentWebTestimonials> getAllByBranchCode(String role, String email, String branchCode, String url);
    StudentWebTestimonials update(Long id, StudentWebTestimonials webTestimonials, String role, String email, MultipartFile testimonialImage, String url);
    void delete(Long id, String role, String email, String url);
    StudentWebTestimonials getById(Long id, String role, String email, String url);
}