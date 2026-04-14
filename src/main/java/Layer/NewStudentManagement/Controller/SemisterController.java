package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.Entity.StudentSemister;
import Layer.NewStudentManagement.Service.SemisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class SemisterController
{
    @Autowired
    SemisterService semisterService;

    @PostMapping("/createSemister")
    public ResponseEntity<StudentSemister> createSemister(@RequestParam String role, @RequestParam String email,
                                                          @RequestBody StudentSemister semister)
    {
        StudentSemister semister1 = semisterService.createSemister(role,email,semister);
        return ResponseEntity.ok(semister1);
    }

    @PutMapping("/updateSemister/{id}")
    public ResponseEntity<StudentSemister> updateSemister(@PathVariable Long id,@RequestParam String role,
                                                          @RequestParam String email,@RequestBody StudentSemister semister)
    {
        StudentSemister semister1 = semisterService.updateSemister(id, role, email, semister);
        return ResponseEntity.ok(semister1);
    }

    @GetMapping("/getAllSemister")
    public ResponseEntity<List<StudentSemister>> getAllSemister(@RequestParam String role,@RequestParam String email)
    {
        List<StudentSemister> semisters = semisterService.getAllSemister(role, email);
        return ResponseEntity.ok(semisters);
    }

    @GetMapping("/getSemisterById/{id}")
    public ResponseEntity<StudentSemister> getSemisterById(@PathVariable Long id,@RequestParam String role,
                                                           @RequestParam String email)
    {
        StudentSemister semister = semisterService.getSemister(id, role, email);
        return ResponseEntity.ok(semister);
    }

    @DeleteMapping("/deleteSemister/{id}")
    public String deleteSemister(@PathVariable Long id, @RequestParam String role,@RequestParam String email)
    {
        semisterService.deleteSemister(id, role, email);
        return "Semister deleted successfully with ID: " + id;
    }

}
