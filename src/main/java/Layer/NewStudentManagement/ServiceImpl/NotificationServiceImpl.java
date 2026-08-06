package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentNotification;
import Layer.NewStudentManagement.Repository.NotificationRepository;
import Layer.NewStudentManagement.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    StaffService staffService;

    public StudentNotification createNotification(StudentNotification notification, String role, String email) {
        if (!staffService.hasPermission(role, email, "POST")) {
            throw new RuntimeException("You don't have permission to create Notification");
        }
        if ("TEACHER".equalsIgnoreCase(role)) {
            if (notification.getClassRoomId() == null || notification.getClassRoomId() <= 0) {
                if (notification.getStudentId() == null || notification.getStudentId() <= 0) {
                    throw new IllegalArgumentException("Teacher must provide either a classRoomId or studentId");
                }
            }
        } else {
            notification.setClassRoomId(null); // only Teacher can set it
            notification.setStudentId(null); // only Teacher can set it
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        notification.setCreatedByEmail(email);
        notification.setRole(role);
        notification.setBranchCode(branchCode);

        return notificationRepository.save(notification);

    }

    @Override
    public StudentNotification updateNotification(StudentNotification notification, Long id, String role, String email) {
        if (!staffService.hasPermission(role, email, "PUT")) {
            throw new RuntimeException("You don't have permission to Update Notification");
        }

        StudentNotification existing = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));

        existing.setNoticeName(notification.getNoticeName());
        existing.setNoticeDescription(notification.getNoticeDescription());

        if ("TEACHER".equalsIgnoreCase(notification.getRole())) {
            existing.setClassRoomId(notification.getClassRoomId());
        } else {
            existing.setClassRoomId(null);
        }

        return notificationRepository.save(existing);

    }

    @Override
    public List<StudentNotification> getNotificationByClassroom(String role, String email, Long classRoomId) {
        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to View Notification");
        }

        return notificationRepository.getNoticesByClassId(classRoomId);

    }

    @Override
    public List<StudentNotification> getNotificationByBranchCode(String role, String email) {
        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to View Notification");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        return notificationRepository.getNoticesByBranchCode(branchCode);

    }

    @Override
    public List<StudentNotification> getNotificationByInstitutionType(String role, String email, String institutionType) {
        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to View Notification");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        return notificationRepository.getNoticesByInstitutionType(branchCode, institutionType);
    }

    @Override
    public void deleteNotification(String role, String email, Long id) {
        if (!staffService.hasPermission(role, email, "DELETE")) {
            throw new RuntimeException("You don't have permission to Delete Notification");
        }
        notificationRepository.deleteById(id);
    }

    @Override
    public StudentNotification getNotificationById(Long id, String role, String email) {
        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to View Notification");
        }
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));
    }

    @Override
    public List<StudentNotification> getNotificationsByStudentId(Long studentId, String role, String email) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to read notifications.");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        return notificationRepository.findByStudentIdAndBranchCode(studentId, branchCode);
    }
}
