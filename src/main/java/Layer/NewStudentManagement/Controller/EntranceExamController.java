package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.Entity.StudentEntranceExam;
import Layer.NewStudentManagement.Service.EntranceExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class EntranceExamController
{

    @Autowired
    EntranceExamService entranceExamService;


    @PostMapping("/createEntranceExam")
    public ResponseEntity<StudentEntranceExam> createEntranceExam (@RequestBody StudentEntranceExam entranceExam, @RequestParam String role,
                                                                  @RequestParam String email)
    {
        StudentEntranceExam entranceExam1 = entranceExamService.createIntranceExam(role,email,entranceExam);
        return ResponseEntity.ok(entranceExam1);

    }

    @GetMapping("/getAllEnranceExam")
    public ResponseEntity<List<StudentEntranceExam>> getEntranceExam (@RequestParam String role, @RequestParam String email)
    {
        List<StudentEntranceExam> entranceExam = entranceExamService.getAllIntranceExams(role, email);
        return ResponseEntity.ok(entranceExam);
    }

    @GetMapping("/getEntranceExamById/{id}")
    public ResponseEntity<StudentEntranceExam> getSEntranceExam(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email)
    {
        StudentEntranceExam entranceExam = entranceExamService.getIntranceExamById(role,email,id);
        return ResponseEntity.ok(entranceExam);

    }

    @PutMapping("/updateEntranceExam/{id}")
    public ResponseEntity<StudentEntranceExam> updateEntranceExam(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email,
            @RequestBody StudentEntranceExam entranceExam)
    {
        StudentEntranceExam entranceExam1 = entranceExamService.updateIntranceExam(id,role,email,entranceExam);
        return ResponseEntity.ok(entranceExam1);
    }

    @DeleteMapping("/deleteEntranceExam/{id}")
    public ResponseEntity<String> deleteEntranceExam(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email)
    {
        entranceExamService.deleteIntranceExam(id,role,email);
        return ResponseEntity.ok("Entrance Exam Delete SuccessFully");
    }

}
