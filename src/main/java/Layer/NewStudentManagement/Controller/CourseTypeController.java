package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StudentCourseTypeDTO;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.CourseTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class CourseTypeController {
    @Autowired
    CourseTypeService courseTypeService;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/createCourseType")
    public ResponseEntity<StudentCourseTypeDTO> createCourseType(@RequestParam String role, @RequestParam String email, @RequestBody StudentCourseTypeDTO courseTypeDTO) {
        StudentCourseTypeDTO createCourseType = courseTypeService.createCourseType(role, email, courseTypeDTO);
        return ResponseEntity.ok(createCourseType);
    }

    @GetMapping("/getAllCourseType")
    public ResponseEntity<Iterable<StudentCourseTypeDTO>> getAllCourseType(@RequestParam String role, @RequestParam String email) {
        Iterable<StudentCourseTypeDTO> courseTypes = courseTypeService.getAllCourseTypes(role, email);
        return ResponseEntity.ok(courseTypes);
    }

    @GetMapping("/getCourseTypeById/{id}")
    public ResponseEntity<StudentCourseTypeDTO> getCourseTypeById(@PathVariable Long id, @RequestParam String role, @RequestParam String email) {
        StudentCourseTypeDTO courseType = courseTypeService.getCourseTypesById(role, email, id);
        return ResponseEntity.ok(courseType);
    }

    @DeleteMapping("/deleteCourseType/{id}")
    public ResponseEntity<String> deleteCourseTypeById(@PathVariable Long id, @RequestParam String role, @RequestParam String email) {
        courseTypeService.deleteCourseType(role, email, id);
        return ResponseEntity.ok("Course Type deleted successfully");
    }

    @GetMapping("/getCourseTypeByGraduationType")
    public ResponseEntity<?> getCourseTypesByGraduationType(
            @RequestParam String role,
            @RequestParam(required = false) String email,
            @RequestParam Long graduationTypeId,
            @RequestParam(required = false) String branchCode,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {

        try {
            // ✅ Validate role
            if (role == null || role.isBlank()) {
                return ResponseEntity.badRequest().body("Role is required");
            }

            String tokenEmail = null;

            // ✅ Extract email from token
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                tokenEmail = jwtUtil.extractEmail(token);
            }

            // ✅ Priority: param email > token email
            String finalEmail = (email != null && !email.isBlank()) ? email : tokenEmail;

            if (finalEmail == null || finalEmail.isBlank()) {
                return ResponseEntity.badRequest().body("Email not found");
            }

            List<StudentCourseTypeDTO> result =
                    courseTypeService.getCourseTypesByGraduationType(role, finalEmail, graduationTypeId, branchCode);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

}
