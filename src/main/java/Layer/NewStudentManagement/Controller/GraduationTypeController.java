package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StudentGraduationTypeDTO;
import Layer.NewStudentManagement.Entity.StudentGraduationType;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.GraduationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class GraduationTypeController
{

    @Autowired
    GraduationTypeService graduationTypeService;

    @Autowired
    JwtUtil jwtUtil;


    @PostMapping("/createGraduationType")
    public ResponseEntity<StudentGraduationTypeDTO> createGraduationType(@RequestParam String role, @RequestParam String email, @RequestBody StudentGraduationTypeDTO graduationType)
    {
        StudentGraduationTypeDTO createGraduationType = graduationTypeService.saveGraduationType(role,email,graduationType);
        return ResponseEntity.ok(createGraduationType);
    }

    @GetMapping("/getAllGraduationType")
    public ResponseEntity<Iterable<StudentGraduationTypeDTO>> getAllGraduationType(@RequestParam String role, @RequestParam String email)
    {
        Iterable<StudentGraduationTypeDTO> graduationType = graduationTypeService.getAllGraduationType(role,email);
        return ResponseEntity.ok(graduationType);
    }
    @GetMapping("/getGraduationTypeById/{id}")
    public ResponseEntity<StudentGraduationTypeDTO> getGraduationTypeById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        StudentGraduationTypeDTO graduationType = graduationTypeService.getGraduationTypeById(id,role,email);
        return ResponseEntity.ok(graduationType);
    }

    @PutMapping("/updateGraduationType/{id}")
    public ResponseEntity<StudentGraduationTypeDTO> updateGraduationType(@PathVariable Long id, @RequestParam String role, @RequestParam String email, @RequestBody StudentGraduationType graduationType)
    {
        StudentGraduationTypeDTO updatedGraduationType = graduationTypeService.updateGraduationType(id,role,email,graduationType);
        return ResponseEntity.ok(updatedGraduationType);
    }

    @DeleteMapping("/deleteGraduationType/{id}")
    public ResponseEntity<Void> deleteGraduationTypeById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        graduationTypeService.deleteGraduationTypeById(id,role,email);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/graduationTypesByStreamName")
    public ResponseEntity<?> getGraduationTypesByStream(
            @RequestParam String role,
            @RequestParam(required = false) String email,
            @RequestParam String streamName,
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

            List<StudentGraduationTypeDTO> result =
                    graduationTypeService.getGraduationTypesByStream(role, finalEmail, streamName, branchCode);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
