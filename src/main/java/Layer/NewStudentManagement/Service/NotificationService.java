package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.Entity.StudentNotification;

import java.util.List;

public interface NotificationService
{
    StudentNotification createNotification(StudentNotification notification, String role, String email);
    StudentNotification updateNotification(StudentNotification notification, Long id, String role, String email);
    List<StudentNotification> getNotificationByClassroom(String role, String email,Long classRoomId);
    List<StudentNotification> getNotificationByBranchCode(String role, String email);
    void deletePeriod(String role, String email,Long id);
    StudentNotification getNotificationById(Long id, String role, String email);

}
