package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.MediumDTO;
import Layer.NewStudentManagement.Entity.StudentMedium;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.MediumService;
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
public class MediumController
{
    @Autowired
    private MediumService mediumService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/createMedium")
    public ResponseEntity<StudentMedium> createMedium(@RequestParam String role, @RequestParam String email, @RequestBody StudentMedium medium)
    {
        StudentMedium createdMedium = mediumService.createMedium(role,email,medium);
        return ResponseEntity.ok(createdMedium);
    }

    @GetMapping("/getAllMedium")
    public ResponseEntity<?> getAllMedium(
            @RequestParam String role,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String branchCode,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {

        try {
            // ✅ Validate role FIRST
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

            List<MediumDTO> result =
                    mediumService.getAllMedium(role, finalEmail, branchCode);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/getMediumById/{id}")
    public ResponseEntity<MediumDTO> getMediumById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        MediumDTO medium = mediumService.getMediumById(id,role,email);
        return ResponseEntity.ok(medium);
    }

    @PutMapping("/updateMedium/{id}")
    public ResponseEntity<StudentMedium> updateMedium(@PathVariable Long id, @RequestParam String role, @RequestParam String email, @RequestBody StudentMedium medium)
    {
        StudentMedium updatedMedium = mediumService.updateMedium(id,role,email,medium);
        return ResponseEntity.ok(updatedMedium);
    }

    @DeleteMapping("/deleteMedium/{id}")
    public ResponseEntity<Void> deleteMediumById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        mediumService.deleteMediumById(id,role,email);
        return ResponseEntity.ok().build();
    }
}
