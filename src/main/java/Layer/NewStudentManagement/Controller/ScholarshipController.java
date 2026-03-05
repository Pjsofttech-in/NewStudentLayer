package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.Entity.StudentScholarship;
import Layer.NewStudentManagement.Service.ScholarshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ScholarshipController
{

    @Autowired
    ScholarshipService scholarshipService;

    @PostMapping("/createScholarship")
    public StudentScholarship createScholarship(
            @RequestParam String role,
            @RequestParam String email,
            @RequestBody StudentScholarship scholarship)
    {
        return scholarshipService.createScholarship(role,email,scholarship);
    }

    @GetMapping("/getScholarshipById/{id}")
    public StudentScholarship getScholarship(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email)
    {
        return scholarshipService.getScholarshipById(id,role,email);
    }

    @GetMapping("/getAllScholarships")
    public List<StudentScholarship> getAll(
            @RequestParam String role,
            @RequestParam String email)
    {
        return scholarshipService.getAllScholarships(role,email);
    }

    @PutMapping("/updateScholarship/{id}")
    public StudentScholarship updateScholarship(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email,
            @RequestBody StudentScholarship scholarship)
    {
        return scholarshipService.updateScholarship(id,role,email,scholarship);
    }

    @DeleteMapping("/deleteScholarship/{id}")
    public void deleteScholarship(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email)
    {
        scholarshipService.deleteScholarship(id,role,email);
    }

}

