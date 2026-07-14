package Layer.NewStudentManagement.Controller.website;

import Layer.NewStudentManagement.Entity.website.StudentWebCourse;
import Layer.NewStudentManagement.Service.website.CourseService;
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
public class CourseController {

    @Autowired
    private CourseService service;

    @PostMapping(value = "/createCourse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentWebCourse> createCourse(
            @RequestPart("course") String courseJson,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String url,
            @RequestParam("courseImage") MultipartFile courseImageFile) throws JsonProcessingException {

        StudentWebCourse webCourse = new ObjectMapper().readValue(courseJson, StudentWebCourse.class);

        return ResponseEntity.ok(service.createCourse(webCourse, role, email, courseImageFile, url));
    }

    @GetMapping("/getAllCourses")
    public ResponseEntity<List<StudentWebCourse>> getAllCoursesByBranchCode(@RequestParam String role,
                                                                     @RequestParam(required = false) String email,
                                                                     @RequestParam String branchCode,
                                                                     @RequestParam String url) {
        return ResponseEntity.ok(service.getAllCoursesByBranchCode(role, email, branchCode, url));
    }

    @GetMapping("/getCourseById/{id}")
    public ResponseEntity<StudentWebCourse> getCourseById(@PathVariable int id,
                                                   @RequestParam String role,
                                                   @RequestParam String email,
                                                   @RequestParam String url) {
        return ResponseEntity.ok(service.getCourseById(id, role, email, url));
    }

    @PutMapping(value = "/updateCourse/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentWebCourse> updateCourse(
            @PathVariable int id,
            @RequestPart("course") String courseJson,
            @RequestPart(value = "courseImage", required = false) MultipartFile courseImage,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String url) throws JsonProcessingException {

        StudentWebCourse webCourse = new ObjectMapper().readValue(courseJson, StudentWebCourse.class);
        StudentWebCourse updated = service.updateCourse(id, webCourse, role, email, courseImage, url);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/deleteCourse/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable int id,
                                               @RequestParam String role,
                                               @RequestParam String email,
                                               @RequestParam String url) {
        service.deleteCourse(id, role, email, url);
        return ResponseEntity.ok("Course deleted successfully");
    }

}