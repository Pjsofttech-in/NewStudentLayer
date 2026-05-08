package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.TimeTableRequestDTO;
import Layer.NewStudentManagement.DTO.TimeTableResponceDTO;
import Layer.NewStudentManagement.Service.TimeTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class TimeTableController
{
    @Autowired
    TimeTableService timeTableService;

    @PostMapping("/createTimeTable")
    public TimeTableResponceDTO createTimeTable(
            @RequestParam String role,
            @RequestParam String email,
            @RequestBody TimeTableRequestDTO timetable) {
        return timeTableService.createTimeTable(role, email, timetable);
    }

    @GetMapping("/getTimeTableById/{id}")
    public TimeTableResponceDTO getTimeTableById(
            @RequestParam String role,
            @RequestParam String email,
            @PathVariable Long id) {
        return timeTableService.getTimeTableById(role, email, id);
    }

    @GetMapping("/getAllTimeTable")
    public List<TimeTableResponceDTO> getAllTimeTable(
            @RequestParam String role,
            @RequestParam String email) {
        return timeTableService.getAllTimeTable(role, email);
    }

    @PutMapping("/updateTimeTable")
    public TimeTableResponceDTO updateTimeTable(
            @RequestParam String role,
            @RequestParam String email,
            @RequestBody TimeTableRequestDTO timetable) {
        return timeTableService.updateTimeTable(role, email, timetable);
    }

    @DeleteMapping("/deleteTimeTable/{id}")
    public String deleteTimeTable(
            @RequestParam String role,
            @RequestParam String email,
            @PathVariable Long id) {
        timeTableService.deleteTimeTable(role, email, id);
        return "Timetable deleted successfully with ID: " + id;
    }

    @GetMapping("/getTimeTableByClassId")
    public List<TimeTableResponceDTO> getTimeTableByClassId(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam Long classId) {
        return timeTableService.getTimeTableByClassId(role, email, classId);
    }



    @PostMapping("/markPeriodOff")
    public ResponseEntity<String> markPeriodOff(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam Long timetableId,
            @RequestParam Long subjectId,
            @RequestParam Long teacherId,
            @RequestParam Long slotId
    ) {
        String message = timeTableService.markPeriodOff(role, email, timetableId, subjectId, teacherId, slotId);
        return ResponseEntity.ok(message);
    }

}
