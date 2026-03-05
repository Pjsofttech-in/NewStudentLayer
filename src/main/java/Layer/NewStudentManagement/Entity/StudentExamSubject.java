package Layer.NewStudentManagement.Entity;

import Layer.NewStudentManagement.Enum.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentExamSubject
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    private String createdByEmail;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String branchCode;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private StudentExam exam;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private StudentSubjectMarks subject;
}
