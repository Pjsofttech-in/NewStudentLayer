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
public class StudentPromotionRecord
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String academicYear;
    private LocalDate promotionDate;

    private Long classroomId;
    private String division;
    private Integer rollNo;

    private Boolean isCurrent = true;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private StudentEntity student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "standard_id")
    private StudentStandard standard;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medium_id")
    private StudentMedium medium;



}
