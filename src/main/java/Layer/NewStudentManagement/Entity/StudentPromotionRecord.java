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
    private Long classroomId;         // Old classroom ID
    private String division;
    private Integer rollNo;
    private String institutionType;

    private String departmentName;

    private Boolean isCurrent = true; // True for latest promotion

    // ============ Student Reference ============
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private StudentEntity student;

    // ============ School Related ============
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "standard_id")
    private StudentStandard standard;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medium_id")
    private StudentMedium medium;

    // ============ Jr. College / UG/PG Related ============
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "degree_id")
    private StudentDegreeName degree;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stream_id")
    private StudentStream stream;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    private StudentGroup group;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "graduation_type_id")
    private StudentGraduationType graduationType;

    private String standardName;
    private String mediumName;
//    private String degreeName;
    private String streamName;
    private String groupName;


}
