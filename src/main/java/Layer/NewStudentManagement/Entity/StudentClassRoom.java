package Layer.NewStudentManagement.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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


    @OneToMany(mappedBy = "classRoom", cascade = CascadeType.ALL)
    private List<StudentEntity> students;


    @OneToMany(mappedBy = "classRoom", cascade = CascadeType.ALL)
    private List<StudentClassRoomTeacherSubject> teacherSubjectAssignments;


}

