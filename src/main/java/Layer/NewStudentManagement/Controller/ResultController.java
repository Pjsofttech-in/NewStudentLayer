package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StudentResultDTO;
import Layer.NewStudentManagement.Entity.StudentResult;
import Layer.NewStudentManagement.Service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class ResultController
{

    @Autowired
    ResultService resultService;


    @PostMapping("/createResult")
    public ResponseEntity<StudentResultDTO> createResult(
            @RequestBody StudentResult result,
            @RequestParam String role,
            @RequestParam String email) {
        return ResponseEntity.ok(resultService.createResult(result, role, email));
    }

    @PutMapping("/updateResult/{id}")
    public ResponseEntity<StudentResultDTO> updateResult(
            @PathVariable Long id,
            @RequestBody StudentResult updated,
            @RequestParam String role,
            @RequestParam String email) {
        return ResponseEntity.ok(resultService.updateResult(id, updated, role, email));
    }

    @DeleteMapping("/deleteResult/{id}")
    public ResponseEntity<String> deleteResult(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        resultService.deleteResult(id, role, email);
        return ResponseEntity.ok("Result deleted successfully");
    }

    @GetMapping("/getAllResult")
    public ResponseEntity<List<StudentResultDTO>> getAllResults(
            @RequestParam String role,
            @RequestParam String email) {
        return ResponseEntity.ok(resultService.getAllResults(role, email));
    }

    @GetMapping("/getResultById/{id}")
    public ResponseEntity<StudentResultDTO> getResultById(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        return ResponseEntity.ok(resultService.getResultById(id, role, email));
    }


    @GetMapping("/getResultByStudentAndAcademicYear")
    public ResponseEntity<?> getAcademicYearReport(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam Long studentId,
            @RequestParam String academicYear
    ) {
        List<StudentResultDTO> results = resultService.getAcademicYearResults(role,email,studentId, academicYear);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/getCurrentResultByStudentId")
    public StudentResultDTO getLatestResultByStudentId(@RequestParam Long studentId,
                                                    @RequestParam String role,
                                                    @RequestParam String email) {
        return resultService.getLatestResultByStudentId(studentId, role, email);
    }

    @GetMapping("/getResultByClassroom")
    public ResponseEntity<List<StudentResultDTO>> getResultsByClassRoom(
            @RequestParam Long classRoomId,
            @RequestParam String role,
            @RequestParam String email) {
        return ResponseEntity.ok(resultService.getResultsByClassRoom(classRoomId, role, email));
    }
}
