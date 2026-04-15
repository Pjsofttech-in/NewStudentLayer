package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StudentDegreeNameDTO;
import Layer.NewStudentManagement.Entity.StudentDegreeName;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.DegreeNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class DegreeNameController
{

    @Autowired
    DegreeNameService degreeNameService;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/createDegreeName")
    public ResponseEntity<StudentDegreeNameDTO> createDegreeName(@RequestParam String role, @RequestParam String email, @RequestBody StudentDegreeNameDTO degreeName)
    {
        StudentDegreeNameDTO createDegreeName = degreeNameService.saveDegreeName(role,email,degreeName);
        return ResponseEntity.ok(createDegreeName);
    }

    @GetMapping("/getAllDegreeName")
    public ResponseEntity<Iterable<StudentDegreeNameDTO>> getAllDegreeName(@RequestParam String role, @RequestParam String email)
    {
        Iterable<StudentDegreeNameDTO> degreeName = degreeNameService.getAllDegreeName(role,email);
        return ResponseEntity.ok(degreeName);
    }
    @GetMapping("/getDegreeNameById/{id}")
    public ResponseEntity<StudentDegreeNameDTO> getDegreeNameById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        StudentDegreeNameDTO degreeName = degreeNameService.getDegreeNameById(id,role,email);
        return ResponseEntity.ok(degreeName);
    }

    @PutMapping("/updateDegreeName/{id}")
    public ResponseEntity<StudentDegreeNameDTO> updateDegreeName(@PathVariable Long id, @RequestParam String role, @RequestParam String email, @RequestBody StudentDegreeName degreeName)
    {
        StudentDegreeNameDTO updateDegreeName = degreeNameService.updateDegreeName(id,role,email,degreeName);
        return ResponseEntity.ok(updateDegreeName);
    }

    @DeleteMapping("/deleteDegreeName/{id}")
    public ResponseEntity<Void> deleteDegreeNameById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        degreeNameService.deleteDegreeNameById(id,role,email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getDegreeNameByGraduationType")
    public ResponseEntity<?> getDegreeNamesByGraduationType(
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

            List<StudentDegreeNameDTO> result =
                    degreeNameService.getDegreeNamesByGraduationType(role, finalEmail, graduationTypeId, branchCode);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

}
