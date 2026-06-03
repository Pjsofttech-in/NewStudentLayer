package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StudentBulkPromotionResponseDTO;
import Layer.NewStudentManagement.DTO.StudentPromotionRequest;
import Layer.NewStudentManagement.DTO.StudentPromotionResponseDTO;
import Layer.NewStudentManagement.Service.StudentPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class StudentPromotionController
{

    @Autowired
    StudentPromotionService studentPromotionService;


    @PostMapping("/studentPramoteInNextClass")
    public ResponseEntity<StudentPromotionResponseDTO> promoteStudent(
            @RequestParam String role,
            @RequestParam String email,
            @RequestBody StudentPromotionRequest requestDTO
    ) {
        StudentPromotionResponseDTO response = studentPromotionService.promoteStudent(
                role,
                email,
                requestDTO.getStudentId(),
                requestDTO.getNewStandardId(),
                requestDTO.getNewMediumId(),
                requestDTO.getNewDegreeNameId(),
                requestDTO.getNewDepartmentName(),
                requestDTO.getNewStreamId(),
                requestDTO.getGroupName(),
                requestDTO.getAcademicYear(),
                requestDTO.getNewClassroomId(),
                requestDTO.getInstitutionType(),
                requestDTO.getGraduationTypeId()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/studentBulkPromoteInNextClass")
    public ResponseEntity<StudentBulkPromotionResponseDTO> promoteStudentBulk(
            @RequestParam String role,
            @RequestParam String email,
            @RequestBody StudentPromotionRequest requestDTO
    ) {
        StudentBulkPromotionResponseDTO response = studentPromotionService.promoteStudentList(
                role,
                email,
                requestDTO.getBulkStudentIds(),
                requestDTO.getNewStandardId(),
                requestDTO.getNewMediumId(),
                requestDTO.getNewDegreeNameId(),
                requestDTO.getNewDepartmentName(),
                requestDTO.getNewStreamId(),
                requestDTO.getGroupName(),
                requestDTO.getAcademicYear(),
                requestDTO.getNewClassroomId(),
                requestDTO.getInstitutionType(),
                requestDTO.getGraduationTypeId()
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
