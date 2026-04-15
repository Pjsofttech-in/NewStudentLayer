package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StandardDTO;
import Layer.NewStudentManagement.DTO.StreamDTO;
import Layer.NewStudentManagement.Entity.StudentStandard;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.StandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class StandardController
{
    @Autowired
    private StandardService standardService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/createStandard")
    public ResponseEntity<StudentStandard> createStandard(@RequestParam String role, @RequestParam String email, @RequestBody StudentStandard standard)
    {
        StudentStandard createdStandard = standardService.createStandard(role,email,standard);
        return ResponseEntity.ok(createdStandard);

    }

    @GetMapping("/getAllStandard")
    public ResponseEntity<?> getAllStandard(
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

            List<StandardDTO> result =
                    standardService.getAllStandard(role, finalEmail, branchCode);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/getStandardById/{id}")
    public ResponseEntity<StandardDTO> getStandardById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        StandardDTO standard = standardService.getStandardById(id,role,email);
        return ResponseEntity.ok(standard);
    }

    @DeleteMapping("/deleteStandard/{id}")
    public ResponseEntity<Void> deleteStandardById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        standardService.deleteStandardById(id,role,email);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/updateStandard/{id}")
    public ResponseEntity<StandardDTO> updateStandard(@PathVariable Long id, @RequestParam String role, @RequestParam String email, @RequestBody StandardDTO standard)
    {
        StandardDTO updatedStandard = standardService.updateStandard(id,role,email,standard);
        return ResponseEntity.ok(updatedStandard);
    }

}
