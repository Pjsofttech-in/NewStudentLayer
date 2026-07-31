package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.*;
import Layer.NewStudentManagement.Service.*;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class DashBoardController {
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
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String branchCode) {

        Map<String, Long> result = studentService.getApplicationCount(role, email, filter, startDate, endDate, institutionType, standardId, mediumId,
                graduationTypeId, streamId, groupName, degreeNameId, departmentName, academicYear, branchCode);
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
            @RequestParam(required = false) Long certificationId,
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String branchCode) {

        return studentService.getGenderCount(role, email, institutionType, standardId, mediumId,
                graduationTypeId, streamId, groupName, degreeNameId, certificationId, departmentName, academicYear, branchCode);
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
            @RequestParam(required = false) String courseType,
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String certificationName,
            @RequestParam(required = false) String institutionType,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String branchCode) {

        if (StringUtils.isNotBlank(institutionType) && "School".equalsIgnoreCase(institutionType)) {
            return studentService.getStudentCountByStandard(role, email, academicYear, mediumName);
        } else {
            return studentService.getStudentCountByClassRoom(
                    role, email, graduationType, standardName, mediumName, streamName, courseType,
                    degreeName, certificationName, departmentName, institutionType, academicYear, branchCode);
        }
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
        return feesCollectService.getReportByYear(role, email, branchCode);
    }

    @GetMapping("/getCollectedFeesByMonth")
    public List<Map<String, Object>> getReportByMonth(@RequestParam String role, @RequestParam String email,
                                                      @RequestParam int year, @RequestParam(required = false) String branchCode) {
        return feesCollectService.getReportByMonth(role, email, year, branchCode);
    }

    @GetMapping("/getCollectedFeesByStandard")
    public List<Map<String, Object>> getReportByStandard(@RequestParam String role, @RequestParam String email, @RequestParam int academicYear) {
        return feesCollectService.getReportByStandard(role, email, academicYear);
    }

    @GetMapping("/getFeesByPaymentMode")
    public List<FeesByPaymentModeDTO> getCollectedFeesByPaymentMode(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam(required = false) String branchCode,
            @RequestParam(required = false) Integer year,
            @RequestParam String institutionType) {

        return feesCollectService.getCollectedFeesByPaymentMode(role, email, branchCode, institutionType, year);
    }

    @GetMapping("/getStudentCountByCastCategory")
    public List<StudentCountByCastCategoryDTO> getStudentCountByCastCategory(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String institutionType,
            @RequestParam(required = false) String branchCode,
            @RequestParam(required = false) String academicYear) {

        return studentService.getStudentCountByCastCategory(role, email, institutionType, branchCode, academicYear);
    }

    @GetMapping("/getCountByGenderAllStandards")
    public List<StudentCountByGenderDTO> getStudentCountByGenderAndAllStandards(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam(required = false) String academicYear) {
        return studentService.getStudentCountByGenderAndAllStandards(role, email, academicYear);
    }

    @GetMapping("/getFeesRevenueByBank")
    public ResponseEntity<Map<String, Double>> getRevenueByBank(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam(required = false) String branchCode) {

        Map<String, Double> response = feesCollectService.getFeesRevenueByBank(role, email, branchCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getFeesRevenueByYear")
    public ResponseEntity<Map<String, List<FeesScheduleChartByYearDTO>>> getFeesRevenueByYear(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam int fromAcademicYear,
            @RequestParam int toAcademicYear,
            @RequestParam(required = false) String branchCode) {

        Map<String, List<FeesScheduleChartByYearDTO>> response = feesService.getYearlyReport(role, email, fromAcademicYear, toAcademicYear, branchCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getFeesRevenueByMonth")
    public ResponseEntity<Map<String, List<FeesScheduleChartByMonthDTO>>> getFeesRevenueByMonth(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam int academicYear,
            @RequestParam(required = false) String branchCode) {

        Map<String, List<FeesScheduleChartByMonthDTO>> response = feesService.getMonthlyReport(role, email, academicYear, branchCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getFeesRevenueByDayInMonth")
    public ResponseEntity<Map<String, List<FeesScheduleChartProjection>>> getFeesRevenueByDayInMonth(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam int academicYear,
            @RequestParam String monthName,
            @RequestParam(required = false) String branchCode) {

        Map<String, List<FeesScheduleChartProjection>> response = feesCollectService.getReportByDayInMonth(role, email, academicYear, monthName, branchCode);
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
                                              @RequestParam String email, @RequestParam String month,
                                              @RequestParam(required = false) String branchCode) {
        return feesService.getMonthlyFeesStatus(role, email, month, branchCode);
    }

    @GetMapping("/getPassFailCount")
    public Map<String, Long> getPassFailCount(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam Long examId,
            @RequestParam Long classroomId
    ) {
        return teacherService.getPassFailCount(role, email, examId, classroomId);
    }

    @GetMapping("/getUpcommingBirthday")
    public ResponseEntity<?> getUpcomingBirthdays(@RequestParam String role, @RequestParam String email) {
        List<UpcomingBirthdayProjection> list =
                studentService.getUpcomingBirthdays(role, email);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/getFeesByClass")
    public ResponseEntity<List<ClassFeesRevenueDTO>> getClassWiseRevenue(
            @RequestParam String role,
            @RequestParam String email) {

        return ResponseEntity.ok(
                feesService.getClassWiseRevenue(role, email)
        );
    }


    @GetMapping("/getDailyCollection")
    public ResponseEntity<?> getDailyFees(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        return ResponseEntity.ok(
                feesCollectService.getDailyCollectedFees(role, email, date)
        );
    }

    @GetMapping("/getFeesByCollectedByWithFilter")
    public ResponseEntity<?> getFees(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String filter,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {

        return ResponseEntity.ok(
                feesCollectService.getCollectedFeesByFilter(role, email, filter, fromDate, toDate)
        );
    }


}
