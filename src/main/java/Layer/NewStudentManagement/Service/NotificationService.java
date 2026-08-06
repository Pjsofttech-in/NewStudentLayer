package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.StudentNotificationDTO;
import Layer.NewStudentManagement.Entity.StudentNotification;

import java.util.List;

public interface NotificationService {
    StudentNotification createNotification(StudentNotification notification, String role, String email);

    StudentNotification updateNotification(StudentNotification notification, Long id, String role, String email);

    List<StudentNotification> getNotificationByClassroom(String role, String email, Long classRoomId);

    List<StudentNotification> getNotificationByBranchCode(String role, String email);

    List<StudentNotification> getNotificationByInstitutionType(String role, String email, String institutionType);

    void deleteNotification(String role, String email, Long id);

    StudentNotification getNotificationById(Long id, String role, String email);

    /**
     * Get all notifications sent to a specific student.
     */
    List<StudentNotification> getNotificationsByStudentId(Long studentId, String role, String email);
}
