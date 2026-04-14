package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.AttendanceCountDTO;
import Layer.NewStudentManagement.DTO.StudentAttendaceDTO;
import Layer.NewStudentManagement.DTO.StudentAttendanceFilterDTO;
import Layer.NewStudentManagement.Entity.StudentAttendance;
import Layer.NewStudentManagement.Service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class AttendanceController
{

    @Autowired
    AttendanceService attendanceService;


    @PostMapping("/markStudentAttendanceByTeacher")
    public ResponseEntity<String> markStudentAttendance(
            @RequestParam List<Integer> rollNos,
            @RequestParam Long classroomId
    ) {
        String result = attendanceService.markStudentsAttendance(rollNos, classroomId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logoutStudentByTeacher")
    public ResponseEntity<String> logoutStudents(
            @RequestParam List<Integer> rollNos,
            @RequestParam Long classroomId
    ) {
        String result = attendanceService.logoutStudents(rollNos, classroomId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/markStudentAttenndance")
    public ResponseEntity<String> markAttendanceFromFace(
            @RequestParam("image") MultipartFile image,
            @RequestParam("branchCode") String branchCode
    ) {
        String result = attendanceService.markAttendanceFromFace(image, branchCode);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/studentLogoutByFace")
    public ResponseEntity<String> logoutStudentFromFace(
            @RequestParam("image") MultipartFile image,
            @RequestParam("branchCode") String branchCode
    ) {
        String result = attendanceService.logoutStudentFromFace(image, branchCode);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/getAttendaceByClassroom")
    public ResponseEntity<Page<StudentAttendaceDTO>> filterAttendance(
            @RequestParam Long classroomId,
            @RequestParam(required = false) String timeFrame,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate customStartDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate customEndDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody(required = false) StudentAttendanceFilterDTO filterDTO) {

        Pageable pageable = PageRequest.of(page, size);
        Page<StudentAttendaceDTO> result = attendanceService.getFilteredAttendance(
                classroomId, filterDTO, timeFrame, customStartDate, customEndDate, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/cardCountForAttendace")
    public ResponseEntity<AttendanceCountDTO> getAttendanceCount(
            @RequestParam Long classroomId,
            @RequestParam String timeFrame,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate customStartDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate customEndDate) {
        return ResponseEntity.ok(attendanceService.getAttendanceCountByTimeFrame(classroomId,timeFrame,customStartDate,customEndDate));
    }


    @GetMapping("/getAllAttendaceByStudentId")
    public ResponseEntity<Page<StudentAttendaceDTO>> getAttendanceByStudent(
            @RequestParam Long studentId,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<StudentAttendaceDTO> result = attendanceService.getAttendanceByStudentId(
                studentId, filter, startDate, endDate, pageable);

        return ResponseEntity.ok(result);
    }





}
