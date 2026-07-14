package Layer.NewStudentManagement.Controller.website;

import Layer.NewStudentManagement.Entity.website.StudentWebTestimonials;
import Layer.NewStudentManagement.Service.website.TestimonialsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://pjsofttech.in")
public class TestimonialsController {

    @Autowired
    private TestimonialsService service;

    @PostMapping(value = "/createTestimonial", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentWebTestimonials> create(
            @RequestPart("testimonial") String testimonialJson,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String url,
            @RequestParam("testimonialImage") MultipartFile testimonialImage) throws JsonProcessingException {

        StudentWebTestimonials webTestimonials = new ObjectMapper().readValue(testimonialJson, StudentWebTestimonials.class);

        return ResponseEntity.ok(service.create(webTestimonials, role, email, testimonialImage, url));
    }

    @GetMapping("/getAllTestimonials")
    public ResponseEntity<List<StudentWebTestimonials>> getAllByBranchCode(@RequestParam String role,
                                                                    @RequestParam(required = false) String email,
                                                                    @RequestParam String branchCode,
                                                                    @RequestParam String url) {
        return ResponseEntity.ok(service.getAllByBranchCode(role, email, branchCode, url));
    }

    @GetMapping("/getTestimonialById/{id}")
    public ResponseEntity<StudentWebTestimonials> getById(@PathVariable Long id,
                                                   @RequestParam String role,
                                                   @RequestParam String email,
                                                   @RequestParam String url) {
        return ResponseEntity.ok(service.getById(id, role, email, url));
    }

    @PutMapping(value = "/updateTestimonial/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentWebTestimonials> update(
            @PathVariable Long id,
            @RequestPart("testimonial") String testimonialJson,
            @RequestPart(value = "testimonialImage", required = false) MultipartFile testimonialImage,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String url) throws JsonProcessingException {

        StudentWebTestimonials webTestimonials = new ObjectMapper().readValue(testimonialJson, StudentWebTestimonials.class);
        return ResponseEntity.ok(service.update(id, webTestimonials, role, email, testimonialImage, url));
    }

    @DeleteMapping("/deleteTestimonial/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id,
                                         @RequestParam String role,
                                         @RequestParam String email,
                                         @RequestParam String url) {
        service.delete(id, role, email, url);
        return ResponseEntity.ok("Testimonial deleted successfully");
    }
}