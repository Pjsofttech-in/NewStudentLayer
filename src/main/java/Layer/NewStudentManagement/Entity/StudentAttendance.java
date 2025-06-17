package Layer.NewStudentManagement.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class StudentAttendance
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rollNo;

    private String branchCode;

    private Long classroomId;

    private String studentName;

    private String systemName;

    private LocalDate date;

    private LocalTime loginTime;

    private LocalTime logoutTime;

    private Long workingMinutes;

    private String status;

}
