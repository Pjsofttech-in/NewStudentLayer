package Layer.NewStudentManagement.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentScheduledPeriod
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "timetable_id")
    private StudentTimetable timetable;

    @ManyToOne(optional = false)
    @JoinColumn(name = "slot_id")
    private StudentPeriod periodSlot;

    @ManyToOne(optional = false)
    @JoinColumn(name = "teacher_id")
    private StudentTeacher teacher;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subject_id")
    private StudentSubject subject;

    private LocalDate periodDate; // date of that scheduled period

    private String status; // e.g. "Scheduled", "Off", "Completed"
}
