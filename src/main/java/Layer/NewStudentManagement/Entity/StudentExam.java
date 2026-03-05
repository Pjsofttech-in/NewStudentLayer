package Layer.NewStudentManagement.Entity;

import Layer.NewStudentManagement.Enum.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentExam
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String examName;
    private LocalDate examDate;
    private String examType;

    @Email
    private String createdByEmail;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String branchCode;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private StudentClassRoom classRoom;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentExamSubject> subjects;
}
