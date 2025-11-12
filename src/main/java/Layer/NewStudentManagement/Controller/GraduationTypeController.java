package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StudentGraduationTypeDTO;
import Layer.NewStudentManagement.Entity.StudentGraduationType;
import Layer.NewStudentManagement.Service.GraduationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class GraduationTypeController
{

    @Autowired
    GraduationTypeService graduationTypeService;


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
    public ResponseEntity<List<StudentGraduationTypeDTO>> getGraduationTypesByStream(
            @RequestParam String role,
            @RequestParam(required = false) String email,
            @RequestParam String streamName,
            @RequestParam(required = false) String branchCode,              // NEW optional filter
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader)
    {
        try {
            String token = null;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);  // Extract token after "Bearer "
            }

            List<StudentGraduationTypeDTO> graduationTypeDTOS =
                    graduationTypeService.getGraduationTypesByStream(role, email, streamName, branchCode, token);

            return ResponseEntity.ok(graduationTypeDTOS);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
        }
    }


}
