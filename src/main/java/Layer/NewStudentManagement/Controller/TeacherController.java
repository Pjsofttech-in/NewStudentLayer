package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.*;
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
    public ResponseEntity<StudentTeacherDTO> createTeacher(
            @RequestParam String role,
            @RequestParam String email,
            @RequestBody TeacherRequestDTO dto) {

        StudentTeacherDTO createdTeacher = teacherService.createTeacher(role, email, dto);
        return new ResponseEntity<>(createdTeacher, HttpStatus.CREATED);
    }


    @GetMapping("/getTeacherById/{id}")
    public ResponseEntity<StudentTeacherDTO> getTeacherById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        StudentTeacherDTO teacher = teacherService.getTeacherById(id,role,email);
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
    public ResponseEntity<Iterable<StudentTeacherDTO>> getAllTeacher(@RequestParam String role, @RequestParam String email)
    {
        Iterable<StudentTeacherDTO> teachers = teacherService.getAllTeacher(role,email);
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/getTeacherByInstitutionType")
    public ResponseEntity<Iterable<StudentTeacherDTO>> getTeacherByInstitutionType(@RequestParam String role, @RequestParam String email, @RequestParam String institutionType)
    {
        Iterable<StudentTeacherDTO> teachers = teacherService.getTeacherByInstitutionType(role,email,institutionType);
        return ResponseEntity.ok(teachers);
    }

    @PostMapping("/teacherLogin")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = teacherService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sendOtpToTeacher")
    public ResponseEntity<String> sendOtp(@RequestBody SendOtpRequest request) {
        return ResponseEntity.ok(teacherService.sendOtp(request.getEmail()));
    }

    @PostMapping("/verifyOtpToTeacher")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(teacherService.verifyOtp(request.getEmail(), request.getOtp()));
    }

    @PostMapping("/resetTeacherPassword")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(teacherService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword()));
    }
}
