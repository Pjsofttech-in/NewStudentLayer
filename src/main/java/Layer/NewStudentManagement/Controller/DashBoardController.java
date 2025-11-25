package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.*;
import Layer.NewStudentManagement.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    TeacherService teacherService;

    @Autowired
    FeesService feesService;

    @Autowired
    AssignmentSubmissionService assignmentSubmissionService;

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
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String branchCode) {

        return studentService.getStudentCountByClassRoom(
                role, email, graduationType, standardName, mediumName, streamName,
                degreeName, departmentName, institutionType, academicYear, branchCode);
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
    public List<Map<String, Object>> getReportByYear(@RequestParam String role, @RequestParam String email, @RequestParam(required = false) String branchCode) {
        return feesCollectService.getReportByYear(role, email,branchCode);
    }

    @GetMapping("/getCollectedFeesByMonth")
    public List<Map<String, Object>> getReportByMonth(@RequestParam String role, @RequestParam String email,
                                                      @RequestParam int year,@RequestParam(required = false) String branchCode) {
        return feesCollectService.getReportByMonth(role,email,year,branchCode);
    }

    @GetMapping("/getCollectedFeesByStandard")
    public List<Map<String, Object>> getReportByStandard(@RequestParam String role, @RequestParam String email) {
        return feesCollectService.getReportByStandard(role, email);
    }

    @GetMapping("/getFeesByPaymentMode")
    public List<FeesByPaymentModeDTO> getCollectedFeesByPaymentMode(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam(required = false) String branchCode,
            @RequestParam(required = false) Integer year,
            @RequestParam String institutionType) {

        return feesCollectService.getCollectedFeesByPaymentMode(role, email, branchCode, institutionType,year);
    }

    @GetMapping("/getStudentCountByCastCategory")
    public List<StudentCountByCastCategoryDTO> getStudentCountByCastCategory(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String institutionType,
            @RequestParam(required = false) String branchCode) {

        return studentService.getStudentCountByCastCategory(role, email, institutionType, branchCode);
    }

    @GetMapping("/getCountByGenderAllStandards")
    public List<StudentCountByGenderDTO> getStudentCountByGenderAndAllStandards(
            @RequestParam String role,
            @RequestParam String email) {
        return studentService.getStudentCountByGenderAndAllStandards(role, email);
    }

    @GetMapping("/getFeesRevenueByBank")
    public ResponseEntity<Map<String, Double>> getRevenueByBank(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam(required = false) String branchCode) {

        Map<String, Double> response = feesCollectService.getFeesRevenueByBank(role, email,branchCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/revenueByStudent")
    public FeesRevenueProjection getFeesRevenueByStudent(@RequestParam String role,
                                                          @RequestParam String email,
                                                          @RequestParam Long studentId) {
        return feesService.getFeesRevenueByStudentId(role, email, studentId);
    }

    @GetMapping("/assignmentsCountByStudent")
    public ResponseEntity<Map<String, Long>> getAssignmentCounts(@RequestParam String role,
                                                                 @RequestParam String email,
                                                                 @RequestParam Long studentId) {
        Map<String, Long> counts = assignmentSubmissionService.getAssignmentCountsByStudent(role, email, studentId);
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/getFeesRevenewByMonth")
    public Map<String, Object> getMonthlyFees(@RequestParam String role,
                                              @RequestParam String email,@RequestParam String month,
                                              @RequestParam(required = false) String branchCode) {
        return feesService.getMonthlyFeesStatus(role, email, month,branchCode);
    }

    @GetMapping("/getPassFailCount")
    public Map<String, Long> getPassFailCount(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam Long examId,
            @RequestParam Long classroomId
    ) {
        return teacherService.getPassFailCount(role,email,examId, classroomId);
    }

}
