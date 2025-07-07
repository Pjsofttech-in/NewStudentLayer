package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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
            @RequestParam String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<String, Long> result = studentService.getApplicationCount(filter, startDate, endDate);
        return ResponseEntity.ok(result);
    }
}
