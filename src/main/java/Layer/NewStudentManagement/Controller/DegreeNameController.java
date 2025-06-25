package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StudentDegreeNameDTO;
import Layer.NewStudentManagement.Entity.StudentDegreeName;
import Layer.NewStudentManagement.Service.DegreeNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class DegreeNameController
{

    @Autowired
    DegreeNameService degreeNameService;

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
    public ResponseEntity<List<StudentDegreeNameDTO>> getDegreeNamesByGraduationType(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam Long graduationTypeId) {
        return ResponseEntity.ok(
                degreeNameService.getDegreeNamesByGraduationType(role, email, graduationTypeId)
        );
    }

}
