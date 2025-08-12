package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.ClassRoomStudentCountProjection;
import Layer.NewStudentManagement.DTO.GenderCountResponse;
import Layer.NewStudentManagement.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class DashBoardController
{
    @Autowired
    StudentService studentService;


    @GetMapping("/getStudentCountForCards")
    public ResponseEntity<Map<String, Long>> getApplicationCounts(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String institutionType,
            @RequestParam(required = false) Long standardId,
            @RequestParam(required = false) Long mediumId,
            @RequestParam(required = false) Long graduationTypeId,
            @RequestParam(required = false) Long streamId,
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) Long degreeNameId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String branchCode) {

        Map<String, Long> result = studentService.getApplicationCount(role, email, filter, startDate, endDate,institutionType,standardId,mediumId,
                graduationTypeId, streamId, groupName,degreeNameId, departmentId, academicYear, branchCode);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/genderCountByBranchCode")
    public GenderCountResponse getGenderCount(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam(required = false) String institutionType,
            @RequestParam(required = false) Long standardId,
            @RequestParam(required = false) Long mediumId,
            @RequestParam(required = false) Long graduationTypeId,
            @RequestParam(required = false) Long streamId,
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) Long degreeNameId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String branchCode) {

        return studentService.getGenderCount(role,email, institutionType,standardId,mediumId,
                graduationTypeId, streamId, groupName,degreeNameId, departmentId, academicYear,branchCode);
    }


    @GetMapping("/getStudentCountByClassrooms")
    public List<ClassRoomStudentCountProjection> getStudentCountByClassRoom(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam(required = false) String graduationType,
            @RequestParam(required = false) String standardName,
            @RequestParam(required = false) String mediumName,
            @RequestParam(required = false) String streamName,
            @RequestParam(required = false) String degreeName,
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String institutionType,
            @RequestParam(required = false) String academicYear)
    {
        return studentService.getStudentCountByClassRoom(
                role, email, graduationType, standardName, mediumName, streamName, degreeName, departmentName, institutionType,academicYear);
    }
}
