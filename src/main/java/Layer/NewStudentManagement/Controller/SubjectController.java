package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.Entity.StudentSubject;
import Layer.NewStudentManagement.Service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class SubjectController
{
    @Autowired
    private SubjectService subjectService;

    @PostMapping("/createSubject")
    public ResponseEntity<StudentSubject> createSubject(@RequestParam String role, @RequestParam String email, @RequestBody StudentSubject subjectName)
    {
        StudentSubject createdSubject = subjectService.createSubject(role,email,subjectName);
        return ResponseEntity.ok(createdSubject);
    }

    @GetMapping("/getSubjectById/{id}")
    public ResponseEntity<StudentSubject> getSubjectById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        StudentSubject subject = subjectService.getSubjectById(id,role,email);
        return ResponseEntity.ok(subject);
    }

    @PutMapping("/updateSubject/{id}")
    public ResponseEntity<StudentSubject> updateSubject(@PathVariable Long id, @RequestParam String role, @RequestParam String email, @RequestBody StudentSubject subjectName)
    {
        StudentSubject updatedSubject = subjectService.updateSubject(id,role,email,subjectName);
        return ResponseEntity.ok(updatedSubject);
    }

    @DeleteMapping("/deleteSubject/{id}")
    public ResponseEntity<Void> deleteSubjectById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        subjectService.deleteSubjectById(id,role,email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAllSubject")
    public ResponseEntity<Iterable<StudentSubject>> getAllSubject(@RequestParam String role, @RequestParam String email)
    {
        Iterable<StudentSubject> subjects = subjectService.getAllSubject(role,email);
        return ResponseEntity.ok(subjects);
    }

}
