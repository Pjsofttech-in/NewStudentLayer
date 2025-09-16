package Layer.NewStudentManagement.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentPeriod
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer periodNo;
    private LocalTime startTime;
    private LocalTime endTime;

    @Email
    private String createdByEmail;
    private String branchCode;
    private String role;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private StudentSubject subject;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private StudentTeacher teacher;

    @ManyToOne
    @JoinColumn(name = "timetable_id")
    private StudentTimetable timetable;
}
