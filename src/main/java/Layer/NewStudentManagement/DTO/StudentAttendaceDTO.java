package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
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
}
