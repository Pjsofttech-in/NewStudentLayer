package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StudentDivisionDTO;
import Layer.NewStudentManagement.Entity.StudentDivision;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.DivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class DivisionController
{
    @Autowired
    private DivisionService divisionService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/createDivision")
    public ResponseEntity<StudentDivision> createDivision(@RequestParam String role, @RequestParam String email, @RequestBody StudentDivision division)
    {
        StudentDivision createdDivision = divisionService.createDivision(role,email,division);
        return ResponseEntity.ok(createdDivision);
    }

    @GetMapping("/getAllDivision")
    public ResponseEntity<?> getAllDivision(
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

            Iterable<StudentDivisionDTO> result =
                    divisionService.getAllDivision(role, finalEmail, branchCode);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
    @GetMapping("/getDivisionById/{id}")
    public ResponseEntity<StudentDivisionDTO> getDivisionById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        StudentDivisionDTO division = divisionService.getDivisionById(id,role,email);
        return ResponseEntity.ok(division);
    }

    @PutMapping("/updateDivision/{id}")
    public ResponseEntity<StudentDivision> updateDivision(@PathVariable Long id, @RequestParam String role, @RequestParam String email, @RequestBody StudentDivision division)
    {
        StudentDivision updatedDivision = divisionService.updateDivision(id,role,email,division);
        return ResponseEntity.ok(updatedDivision);
    }

    @DeleteMapping("/deleteDivision/{id}")
    public ResponseEntity<Void> deleteDivisionById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        divisionService.deleteDivisionById(id,role,email);
        return ResponseEntity.ok().build();
    }
}
