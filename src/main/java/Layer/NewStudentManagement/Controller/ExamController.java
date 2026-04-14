package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StudentExamDTO;
import Layer.NewStudentManagement.Entity.StudentExam;
import Layer.NewStudentManagement.Service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
public class ExamController
{
    @Autowired
    private ExamService examService;

    @PostMapping("/createExamWithSubject")
    public ResponseEntity<StudentExamDTO> createExamWithSubjects(
            @RequestBody StudentExam exam,
            @RequestParam List<Long> subjectIds,
            @RequestParam String role,
            @RequestParam String email) {
        return ResponseEntity.ok(examService.createExamWithSubjects(exam, subjectIds, role, email));
    }

    @PutMapping("/updateExamWithSubject/{id}")
    public ResponseEntity<StudentExamDTO> updateExamWithSubjects(
            @PathVariable Long id,
            @RequestBody StudentExam updated,
            @RequestParam(required = false) List<Long> subjectIds,
            @RequestParam String role,
            @RequestParam String email) {
        return ResponseEntity.ok(examService.updateExamWithSubjects(id, updated, subjectIds, role, email));
    }

    @GetMapping("/getExamWithSubjectById/{id}")
    public ResponseEntity<StudentExamDTO> getExamById(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        return ResponseEntity.ok(examService.getExamById(id, role, email));
    }

    @GetMapping("/getAllExamWithSubject")
    public ResponseEntity<List<StudentExamDTO>> getExams(
            @RequestParam String role,
            @RequestParam String email) {
        return ResponseEntity.ok(examService.getExams(role, email));
    }

    @DeleteMapping("/deleteExam/{id}")
    public ResponseEntity<String> deleteExam(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        examService.deleteExam(id, role, email);
        return ResponseEntity.ok("Exam deleted successfully");
    }

    @DeleteMapping("/RemoveSubjectFromExam/{examId}/subject/{subjectId}")
    public ResponseEntity<String> removeSubjectFromExam(
            @RequestParam Long examId,
            @RequestParam Long subjectId,
            @RequestParam String role,
            @RequestParam String email) {
        examService.removeSubjectFromExam(examId, subjectId, role, email);
        return ResponseEntity.ok("Subject removed from exam successfully");
    }

    @GetMapping("/getExamByClassId")
    public ResponseEntity<List<StudentExamDTO>> getExamsByClassId(@RequestParam Long classId,
                                                @RequestParam String role,
                                                @RequestParam String email) {
        return ResponseEntity.ok(examService.getExamsByClassId(classId, role, email));
    }

}
