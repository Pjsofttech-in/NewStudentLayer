package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.MediumDTO;
import Layer.NewStudentManagement.Entity.StudentMedium;
import Layer.NewStudentManagement.Service.MediumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class MediumController
{
    @Autowired
    private MediumService mediumService;

    @PostMapping("/createMedium")
    public ResponseEntity<StudentMedium> createMedium(@RequestParam String role, @RequestParam String email, @RequestBody StudentMedium medium)
    {
        StudentMedium createdMedium = mediumService.createMedium(role,email,medium);
        return ResponseEntity.ok(createdMedium);
    }

    @GetMapping("/getAllMedium")
    public ResponseEntity<Iterable<MediumDTO>> getAllMedium(@RequestParam String role,
                                                            @RequestParam(required = false) String email,
                                                            @RequestHeader(value = "Authorization", required = false) String authorizationHeader)
    {
        try {
            String token = null;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);  // Extract token after "Bearer "
            }

            List<MediumDTO> medium = mediumService.getAllMedium(role, email, token);
            return ResponseEntity.ok(medium);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
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
