package Layer.NewStudentManagement.Entity;

import Layer.NewStudentManagement.Enum.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class StudentAssignmentSubmission
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileUrl;
    private String remarks;
    private LocalDate submittedDate;
    private String status;

    private String createdByEmail;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String branchCode;
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private StudentAssignment assignment;
}
