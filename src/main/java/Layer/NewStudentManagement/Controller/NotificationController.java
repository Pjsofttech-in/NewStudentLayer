package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.Entity.StudentNotification;
import Layer.NewStudentManagement.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NotificationController
{

    @Autowired
    NotificationService notificationService;

    @PostMapping("/createNotification")
    public ResponseEntity<StudentNotification> createNotification(@RequestBody StudentNotification notification,
                                                                  @RequestParam String role, @RequestParam String email)
    {
        StudentNotification notification1 = notificationService.createNotification(notification, role, email);
        return ResponseEntity.ok(notification1);
    }

    @GetMapping("/getNotificationById/{id}")
    public ResponseEntity<StudentNotification> getNotificationById(@PathVariable Long id,
                                                                  @RequestParam String role, @RequestParam String email)
    {
        StudentNotification notification = notificationService.getNotificationById(id, role, email);
        return ResponseEntity.ok(notification);
    }

    @PutMapping("/updateNotification/{id}")
    public ResponseEntity<StudentNotification> updateNotification(@RequestBody StudentNotification notification,@PathVariable Long id,
                                                                  @RequestParam String role, @RequestParam String email)
    {
        StudentNotification notification1 = notificationService.updateNotification(notification, id,role, email);
        return ResponseEntity.ok(notification1);
    }

    @GetMapping("/getNotificationByClassRoom")
    public ResponseEntity<List<StudentNotification>> getNotificationByClassroom(@RequestParam String role, @RequestParam String email,@RequestParam Long classId)
    {
        List<StudentNotification> notification = notificationService.getNotificationByClassroom(role, email,classId);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/getNotificationByBranchCode")
    public ResponseEntity<List<StudentNotification>> getNotificationByBranchCode(@RequestParam String role, @RequestParam String email)
    {
        List<StudentNotification> notification = notificationService.getNotificationByBranchCode(role, email);
        return ResponseEntity.ok(notification);
    }
}
