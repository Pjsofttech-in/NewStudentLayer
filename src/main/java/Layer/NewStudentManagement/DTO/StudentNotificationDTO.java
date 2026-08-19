package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentNotificationDTO {
    private Long id;
    private String noticeName;
    private String noticeDescription;
    private String institutionType;
    private LocalDate createdAt;
    private Long classRoomId;
    private String createdByEmail;
    private String role;
    private String branchCode;

    // Only returning the student ID, not the whole StudentEntity
    private Long studentId;
}