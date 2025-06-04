package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.TeacherRequestDTO;
import Layer.NewStudentManagement.Entity.StudentTeacher;
import Layer.NewStudentManagement.Security.LoginRequest;
import Layer.NewStudentManagement.Security.LoginResponse;
import Layer.NewStudentManagement.Service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class TeacherController
{
    @Autowired
    private TeacherService teacherService;

    @PostMapping("/createTeacher")
    public ResponseEntity<StudentTeacher> createTeacher(
            @RequestParam String role,
            @RequestParam String email,
            @RequestBody TeacherRequestDTO dto) {

        StudentTeacher createdTeacher = teacherService.createTeacher(role, email, dto);
        return new ResponseEntity<>(createdTeacher, HttpStatus.CREATED);
    }


    @GetMapping("/getTeacherById/{id}")
    public ResponseEntity<StudentTeacher> getTeacherById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        StudentTeacher teacher = teacherService.getTeacherById(id,role,email);
        return ResponseEntity.ok(teacher);
    }

    @PutMapping("/updateTeacher/{id}")
    public ResponseEntity<StudentTeacher> updateTeacher(@PathVariable Long id, @RequestParam String role, @RequestParam String email, @RequestBody TeacherRequestDTO teacher)
    {
        StudentTeacher updatedTeacher = teacherService.updateTeacher(id,role,email,teacher);
        return ResponseEntity.ok(updatedTeacher);
    }

    @DeleteMapping("/deleteTeacher/{id}")
    public ResponseEntity<Void> deleteTeacherById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        teacherService.deleteTeacherById(id,role,email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAllTeacher")
    public ResponseEntity<Iterable<StudentTeacher>> getAllTeacher(@RequestParam String role, @RequestParam String email)
    {
        Iterable<StudentTeacher> teachers = teacherService.getAllTeacher(role,email);
        return ResponseEntity.ok(teachers);
    }

    @PostMapping("/teacherLogin")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = teacherService.login(request);
        return ResponseEntity.ok(response);
    }
}
