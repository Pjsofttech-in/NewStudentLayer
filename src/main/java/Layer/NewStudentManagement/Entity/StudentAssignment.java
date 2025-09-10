package Layer.NewStudentManagement.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class StudentAssignment
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String assignmentTitle;
    private String description;
    private LocalDate dueDate;
    private LocalDate createdDate;
    private String image;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;


    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private StudentTeacher teacher;

    @ManyToOne
    @JoinColumn(name = "classroom_id", nullable = false)
    private StudentClassRoom classRoom;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentAssignmentSubmission> submissions;


}
