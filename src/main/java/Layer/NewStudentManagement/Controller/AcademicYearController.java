package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.Entity.StudentAcademicYear;
import Layer.NewStudentManagement.Service.AcademicYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class AcademicYearController
{

    @Autowired
    AcademicYearService academicYearService;

    @PostMapping("/createAcademicYear")
    public ResponseEntity<StudentAcademicYear> createAcademicYear(@RequestParam String role, @RequestParam String email, @RequestBody StudentAcademicYear academicYear)
    {
        StudentAcademicYear academicYear1 = academicYearService.createAcademicYear(role,email,academicYear);
        return ResponseEntity.ok(academicYear1);
    }

    @GetMapping("/getAcademicYearById/{id}")
    public ResponseEntity<StudentAcademicYear> getAcademicYearById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        StudentAcademicYear academicYear = academicYearService.getAcademicYearById(id,role,email);
        return ResponseEntity.ok(academicYear);
    }

    @GetMapping("/getAllAcademicYear")
    public ResponseEntity<Iterable<StudentAcademicYear>> getAllAcademicYear(@RequestParam String role, @RequestParam String email)
    {
        Iterable<StudentAcademicYear> academicYear = academicYearService.getAllAcademicYear(role,email);
        return ResponseEntity.ok(academicYear);
    }

    @PutMapping("/updateAcademicYear/{id}")
    public ResponseEntity<StudentAcademicYear> updateAcademicYear(@PathVariable Long id, @RequestParam String role, @RequestParam String email, @RequestBody StudentAcademicYear academicYear)
    {
        StudentAcademicYear academicYear1 = academicYearService.updateAcademicYear(id,role,email,academicYear);
        return ResponseEntity.ok(academicYear1);
    }

    @DeleteMapping("/deleteAcademicYear/{id}")
    public ResponseEntity<Void> deleteAcademicYearById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        academicYearService.deleteAcademicYearById(id,role,email);
        return ResponseEntity.ok().build();
    }


}
