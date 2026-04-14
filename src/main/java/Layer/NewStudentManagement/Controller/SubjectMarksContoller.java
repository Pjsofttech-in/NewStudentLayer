package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.SubjectMarksDTO;
import Layer.NewStudentManagement.Entity.StudentSubjectMarks;
import Layer.NewStudentManagement.Service.SubjectMarksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class SubjectMarksContoller
{
    @Autowired
    private SubjectMarksService subjectMarksService;

    @PostMapping("/createSubjectMarks")
    public SubjectMarksDTO createSubjectMarks(@RequestBody StudentSubjectMarks subject,
                                              @RequestParam String role,
                                              @RequestParam String email) {
        return subjectMarksService.createSubject(subject, role, email);
    }

    @GetMapping("/getSubjectMarks")
    public List<SubjectMarksDTO> getAllSubjectMarks(@RequestParam String role,
                                                        @RequestParam String email) {
        return subjectMarksService.getSubjects(role, email);
    }

    @GetMapping("/getSubjectMarksById/{id}")
    public SubjectMarksDTO getAllSubjectMarks(@PathVariable Long id,@RequestParam String role,
                                                        @RequestParam String email) {
        return subjectMarksService.getSubjectsById(id,role, email);
    }

    @GetMapping("/getSubjectMarksByClassroomId")
    public List<SubjectMarksDTO> getAllSubjectMarksByClassRoom(@RequestParam Long classroomId,@RequestParam String role,
                                              @RequestParam String email) {
        return subjectMarksService.getSubjectsByClassroomId(classroomId,role, email);
    }


    @PutMapping("/updateSubjectMarks/{id}")
    public SubjectMarksDTO updateSubjectMarks(@PathVariable Long id,
                                      @RequestBody StudentSubjectMarks subject,
                                      @RequestParam String role,
                                      @RequestParam String email) {
        return subjectMarksService.updateSubject(id, subject, role, email);
    }

    @DeleteMapping("/deleteSubjectMarks/{id}")
    public String deleteSubjectMarks(@PathVariable Long id,
                         @RequestParam String role,
                         @RequestParam String email) {
        subjectMarksService.deleteSubject(id, role, email);
        return "Subject deleted successfully";
    }


    @GetMapping("/getSubjectbyExamId")
    public ResponseEntity<List<SubjectMarksDTO>> getSubjectsByExamId(@RequestParam String role,
                                                                     @RequestParam String email,
                                                                     @RequestParam Long examId) {
        List<SubjectMarksDTO> subjects = subjectMarksService.getSubjectsByExamId(role, email, examId);
        return ResponseEntity.ok(subjects);
    }
}
