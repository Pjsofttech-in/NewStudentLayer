package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StudentPromotionResponseDTO;
import Layer.NewStudentManagement.Entity.StudentPromotionRecord;
import Layer.NewStudentManagement.Service.StudentPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
