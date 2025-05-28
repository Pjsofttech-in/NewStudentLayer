package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.Entity.StudentDivision;
import Layer.NewStudentManagement.Service.DivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class DivisionController
{
    @Autowired
    private DivisionService divisionService;

    @PostMapping("/createDivision")
    public ResponseEntity<StudentDivision> createDivision(@RequestParam String role, @RequestParam String email, @RequestBody StudentDivision division)
    {
        StudentDivision createdDivision = divisionService.createDivision(role,email,division);
        return ResponseEntity.ok(createdDivision);
    }

    @GetMapping("/getAllDivision")
    public ResponseEntity<Iterable<StudentDivision>> getAllDivision(@RequestParam String role, @RequestParam String email)
    {
        Iterable<StudentDivision> divisions = divisionService.getAllDivision(role,email);
        return ResponseEntity.ok(divisions);
    }
    @GetMapping("/getDivisionById/{id}")
    public ResponseEntity<StudentDivision> getDivisionById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        StudentDivision division = divisionService.getDivisionById(id,role,email);
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
