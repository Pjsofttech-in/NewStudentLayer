package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StudentSubjectDTO;
import Layer.NewStudentManagement.Entity.StudentSubject;
import Layer.NewStudentManagement.Service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class SubjectController
{
    @Autowired
    private SubjectService subjectService;

    @PostMapping("/createSubject")
    public ResponseEntity<StudentSubjectDTO> createSubject( @RequestBody StudentSubjectDTO dto,@RequestParam String role, @RequestParam String email)
    {
        StudentSubjectDTO createdSubject = subjectService.saveSubject(dto,role,email);
        return ResponseEntity.ok(createdSubject);
    }

    @GetMapping("/getSubjectById/{id}")
    public ResponseEntity<StudentSubjectDTO> getSubjectById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        StudentSubjectDTO subject = subjectService.getSubjectById(id,role,email);
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
    public ResponseEntity<Iterable<StudentSubjectDTO>> getAllSubject(@RequestParam String role, @RequestParam String email)
    {
        Iterable<StudentSubjectDTO> subjects = subjectService.getAllSubject(role,email);
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/getSubjectByInstitutionType")
    public ResponseEntity<List<StudentSubjectDTO>> getSubjects(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String institutionType,
            @RequestParam(required = false) String graduationTypeName,
            @RequestParam(required = false) String streamName,
            @RequestParam(required = false) String degreeName,
            @RequestParam(required = false) String departmentName) {

        List<StudentSubjectDTO> subjects = subjectService.getSubjects(role, email, institutionType, graduationTypeName, streamName, degreeName, departmentName);
        return ResponseEntity.ok(subjects);
    }


    @GetMapping("/getSubjectByTeacher")
    public ResponseEntity<List<StudentSubjectDTO>> getSubjectsByTeacherId( @RequestParam String role,
                                                                        @RequestParam String email,
                                                                        @RequestParam Long teacherId) {
        List<StudentSubjectDTO> subjects = subjectService.getSubjectsByTeacherId(role, email, teacherId);
        return ResponseEntity.ok(subjects);
    }
}
