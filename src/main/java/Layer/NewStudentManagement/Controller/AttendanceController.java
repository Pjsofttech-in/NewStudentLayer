package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.Service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@RestController
public class AttendanceController
{

    @Autowired
    AttendanceService attendanceService;

    @PostMapping("/markStudentAttenndance")
    public ResponseEntity<String> markAttendance(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "branch_code") String branchCode,
            @RequestParam(value = "classroomId", required = false) String classroomId,
            @RequestParam(value = "system_name", defaultValue = "student-sys") String systemName
    ) {
        try {
            String message = attendanceService.markAttendance(image, systemName, branchCode, classroomId);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

}
