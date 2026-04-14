package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.EmailRequestDTO;
import Layer.NewStudentManagement.Entity.StudentEmail;
import Layer.NewStudentManagement.Service.StudentEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class StudentEmailController
{
    @Autowired
    StudentEmailService emailService;

    @PostMapping("/sendEmailToStudent")
    public ResponseEntity<String> sendEmail(@RequestParam String role,@RequestParam String email, @RequestBody EmailRequestDTO dto) {
        String result = emailService.sendEmailToStudents(role, email, dto);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/getAllSendEmailForStudent")
    public ResponseEntity<List<StudentEmail>> getAllSentEmails(@RequestParam String role,@RequestParam String email) {
        return ResponseEntity.ok(emailService.getAllSentEmails(role, email));
    }
}
