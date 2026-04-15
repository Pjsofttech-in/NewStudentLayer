package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.Entity.StudentScholarship;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.ScholarshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class ScholarshipController
{

    @Autowired
    ScholarshipService scholarshipService;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/createScholarship")
    public StudentScholarship createScholarship(
            @RequestParam String role,
            @RequestParam String email,
            @RequestBody StudentScholarship scholarship)
    {
        return scholarshipService.createScholarship(role,email,scholarship);
    }

    @GetMapping("/getScholarshipById/{id}")
    public StudentScholarship getScholarship(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email)
    {
        return scholarshipService.getScholarshipById(id,role,email);
    }

    @GetMapping("/getAllScholarships")
    public ResponseEntity<?> getAllScholarships(
            @RequestParam String role,
            @RequestParam(required = false) String email,
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

            List<StudentScholarship> result =
                    scholarshipService.getAllScholarships(role, finalEmail, branchCode);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/updateScholarship/{id}")
    public StudentScholarship updateScholarship(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email,
            @RequestBody StudentScholarship scholarship)
    {
        return scholarshipService.updateScholarship(id,role,email,scholarship);
    }

    @DeleteMapping("/deleteScholarship/{id}")
    public void deleteScholarship(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email)
    {
        scholarshipService.deleteScholarship(id,role,email);
    }

}
