package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.Entity.StudentScholarship;
import Layer.NewStudentManagement.Service.ScholarshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class ScholarshipController
{

    @Autowired
    ScholarshipService scholarshipService;

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
    public ResponseEntity<List<StudentScholarship>> getAll(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam(required = false) String branchCode,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader)
    {
        try {
            String token = null;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);  // Extract token after "Bearer "
            }

            List<StudentScholarship> scholarships = scholarshipService.getAllScholarships(role, email,branchCode, token);
            return ResponseEntity.ok(scholarships);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
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

