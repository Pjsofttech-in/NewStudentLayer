package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.Entity.StudentStandard;
import Layer.NewStudentManagement.Service.StandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class StandardController
{
    @Autowired
    private StandardService standardService;

    @PostMapping("/createStandard")
    public ResponseEntity<StudentStandard> createStandard(@RequestParam String role, @RequestParam String email, @RequestBody StudentStandard standard)
    {
        StudentStandard createdStandard = standardService.createStandard(role,email,standard);
        return ResponseEntity.ok(createdStandard);

    }

    @GetMapping("/getAllStandard")
    public ResponseEntity<Iterable<StudentStandard>> getAllStandard(@RequestParam String role, @RequestParam String email)
    {
        Iterable<StudentStandard> standards = standardService.getAllStandard(role,email);
        return ResponseEntity.ok(standards);
    }

    @GetMapping("/getStandardById/{id}")
    public ResponseEntity<StudentStandard> getStandardById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        StudentStandard standard = standardService.getStandardById(id,role,email);
        return ResponseEntity.ok(standard);
    }

    @DeleteMapping("/deleteStandard/{id}")
    public ResponseEntity<Void> deleteStandardById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        standardService.deleteStandardById(id,role,email);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/updateStandard/{id}")
    public ResponseEntity<StudentStandard> updateStandard(@PathVariable Long id, @RequestParam String role, @RequestParam String email, @RequestBody StudentStandard standard)
    {
        StudentStandard updatedStandard = standardService.updateStandard(id,role,email,standard);
        return ResponseEntity.ok(updatedStandard);
    }

}
