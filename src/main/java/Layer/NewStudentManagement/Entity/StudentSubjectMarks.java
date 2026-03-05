package Layer.NewStudentManagement.Entity;

import Layer.NewStudentManagement.Enum.Role;
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
public class StudentSubjectMarks
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subjectName;
    private Integer passingMarks;
    private Integer maxMarks;

    @Email
    private String createdByEmail;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String branchCode;
    @ManyToOne
    @JoinColumn(name = "classroom_id", nullable = false)
    private StudentClassRoom classRoom;
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    private List<StudentExamSubject> examSubjects;
}
