package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StudentPromotionRequest;
import Layer.NewStudentManagement.DTO.StudentPromotionResponseDTO;
import Layer.NewStudentManagement.Entity.StudentPromotionRecord;
import Layer.NewStudentManagement.Service.StudentPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
            @RequestBody StudentPromotionRequest request,
            @RequestParam String role,
            @RequestParam String email) {

        StudentPromotionResponseDTO response = studentPromotionService.promoteStudent(
                role, email, request.getStudentId(),
                request.getNewStandardId(), request.getNewMediumId(),
                request.getNewDegreeNameId(), request.getNewDepartmentId(),
                request.getNewStreamId(), request.getGroupName(),
                request.getAcademicYear(), request.getNewClassroomId()
        );

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
