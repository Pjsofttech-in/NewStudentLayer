package Layer.NewStudentManagement.Entity;

import Layer.NewStudentManagement.Enum.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentTimetable
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate createdDate = LocalDate.now();
    private String dayOfWeek;

    @Email
    private String createdByEmail;
    private String branchCode;
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(optional = false)
    @JoinColumn(name = "classroom_id")
    private StudentClassRoom classroom;

    @OneToMany(mappedBy = "timetable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentScheduledPeriod> scheduledPeriods = new ArrayList<>();

}
