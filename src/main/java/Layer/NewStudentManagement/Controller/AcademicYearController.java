package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.Entity.StudentAcademicYear;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.AcademicYearService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class AcademicYearController
{

    @Autowired
    AcademicYearService academicYearService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/createAcademicYear")
    public ResponseEntity<StudentAcademicYear> createAcademicYear(@RequestParam String role, @RequestParam String email, @RequestBody StudentAcademicYear academicYear)
    {
        StudentAcademicYear academicYear1 = academicYearService.createAcademicYear(role,email,academicYear);
        return ResponseEntity.ok(academicYear1);
    }

    @GetMapping("/getAcademicYearById/{id}")
    public ResponseEntity<StudentAcademicYear> getAcademicYearById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        StudentAcademicYear academicYear = academicYearService.getAcademicYearById(id,role,email);
        return ResponseEntity.ok(academicYear);
    }

    @GetMapping("/getAllAcademicYear")
    public ResponseEntity<?> getAllAcademicYear(
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

            List<StudentAcademicYear> result =
                    academicYearService.getAllAcademicYear(role, finalEmail, branchCode);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/updateAcademicYear/{id}")
    public ResponseEntity<StudentAcademicYear> updateAcademicYear(@PathVariable Long id, @RequestParam String role, @RequestParam String email, @RequestBody StudentAcademicYear academicYear)
    {
        StudentAcademicYear academicYear1 = academicYearService.updateAcademicYear(id,role,email,academicYear);
        return ResponseEntity.ok(academicYear1);
    }

    @DeleteMapping("/deleteAcademicYear/{id}")
    public ResponseEntity<Void> deleteAcademicYearById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        academicYearService.deleteAcademicYearById(id,role,email);
        return ResponseEntity.ok().build();
    }


}
