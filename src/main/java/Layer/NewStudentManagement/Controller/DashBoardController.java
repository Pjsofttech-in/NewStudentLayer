package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.*;
import Layer.NewStudentManagement.Service.AttendanceService;
import Layer.NewStudentManagement.Service.FeesCollectService;
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

    @Autowired
    AttendanceService attendanceService;

    @Autowired
    FeesCollectService feesCollectService;

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


    @GetMapping("/getAttendaceCountByStudentId")
    public ResponseEntity<Map<String, Long>> getAttendanceCount(
            @RequestParam Long studentId,
            @RequestParam(required = false, defaultValue = "all") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<String, Long> counts = attendanceService.getAttendanceCount(studentId, filter, startDate, endDate);
        return ResponseEntity.ok(counts);
    }


    @GetMapping("/getCollectedFeesByYear")
    public List<Map<String, Object>> getReportByYear(@RequestParam String role, @RequestParam String email) {
        return feesCollectService.getReportByYear(role, email);
    }

    @GetMapping("/getCollectedFeesByMonth")
    public List<Map<String, Object>> getReportByMonth(@RequestParam String role, @RequestParam String email, @RequestParam int year) {
        return feesCollectService.getReportByMonth(role,email,year);
    }

    @GetMapping("/getCollectedFeesByStandard")
    public List<Map<String, Object>> getReportByStandard(@RequestParam String role, @RequestParam String email) {
        return feesCollectService.getReportByStandard(role, email);
    }

    @GetMapping("/getFeesByPaymentMode")
    public List<FeesByPaymentModeDTO> getCollectedFeesByPaymentMode(
            @RequestParam String role, @RequestParam String email,
            @RequestParam String institutionType) {
        return feesCollectService.getCollectedFeesByPaymentMode(role,email, institutionType);
    }

    @GetMapping("/getStudentCountByCastCategory")
    public List<StudentCountByCastCategoryDTO> getStudentCountByCastCategory(
            @RequestParam String role, @RequestParam String email,
            @RequestParam String institutionType) {
        return studentService.getStudentCountByCastCategory(role,email, institutionType);
    }

    @GetMapping("/getCountByGenderAllStandards")
    public List<StudentCountByGenderDTO> getStudentCountByGenderAndAllStandards(
            @RequestParam String role,
            @RequestParam String email) {
        return studentService.getStudentCountByGenderAndAllStandards(role, email);
    }

}
