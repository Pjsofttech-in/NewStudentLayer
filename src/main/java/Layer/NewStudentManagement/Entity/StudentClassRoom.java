package Layer.NewStudentManagement.Entity;



import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentClassRoom
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 9)
    private String year;
    private LocalTime startTime;
    private LocalTime endTime;
    private String groupName;
    private String institutionType;
    private LocalDate createdDate;

    @Email
    private String createdByEmail;

    private String role;
    private String branchCode;


    @ManyToOne
    @JoinColumn(name = "medium_id")
    private StudentMedium medium;

    @ManyToOne
    @JoinColumn(name = "division_id")
    private StudentDivision division;

    @ManyToOne
    @JoinColumn(name = "standard_id")
    private StudentStandard standard;

    @ManyToOne
    @JoinColumn(name = "graduation_type_id", nullable = true)
    private StudentGraduationType graduationType; // optional

    @ManyToOne
    @JoinColumn(name = "stream_id", nullable = true)
    private StudentStream stream;

    @ManyToOne
    @JoinColumn(name = "degree_name_id", nullable = true)
    private StudentDegreeName degreeName;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = true)
    private StudentDepartment department;

    @OneToMany(mappedBy = "classRoom", cascade = CascadeType.ALL)
    private List<StudentEntity> students;

    @OneToMany(mappedBy = "classRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentClassRoomTeacherSubject> teacherSubjectAssignments;


}

