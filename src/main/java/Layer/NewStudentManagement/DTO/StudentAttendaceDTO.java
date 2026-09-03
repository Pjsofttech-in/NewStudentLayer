package Layer.NewStudentManagement.DTO;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class StudentAttendaceDTO
{
    private Long id;
    private int rollNo;
    private String branchCode;
    private Long classroomId;
    private String studentName;
    private LocalDate date;
    private LocalTime loginTime;
    private LocalTime logoutTime;
    private Long workingMinutes;
    private String status;
    private Long studentId;

    private Long scheduledPeriodId;
    private String subjectName;
}
