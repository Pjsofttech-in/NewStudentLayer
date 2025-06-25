package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StudentPromotionResponseDTO;
import Layer.NewStudentManagement.Entity.StudentPromotionRecord;
import Layer.NewStudentManagement.Service.StudentPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class StudentPromotionController
{

    @Autowired
    StudentPromotionService studentPromotionService;


    @PostMapping("/studentPramoteInNextClass")
    public ResponseEntity<StudentPromotionResponseDTO> promoteStudent(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam Long studentId,
            @RequestParam Long standardId,
            @RequestParam Long mediumId,
            @RequestParam String academicYear
    ) {
        StudentPromotionResponseDTO response = studentPromotionService.promoteStudent(role, email, studentId, standardId, mediumId, academicYear);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/getPromoteHistoryByStudentId")
    public ResponseEntity<StudentPromotionResponseDTO> getPromotionDetails(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam Long studentId
    ) {
        StudentPromotionResponseDTO response = studentPromotionService.getPromotionInfoById(role, email, studentId);
        return ResponseEntity.ok(response);
    }

}
