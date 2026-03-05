package Layer.NewStudentManagement.Entity;

import Layer.NewStudentManagement.Enum.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentEmail
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    @Lob // For long HTML body
    @Column(columnDefinition = "TEXT")
    private String body;

    private LocalDateTime sentAt;

    @Column(columnDefinition = "TEXT")
    private String studentIds;

    private String sentByEmail;

    private LocalDateTime scheduledAt;  // Store scheduled time (can be null)
    private boolean isScheduled;
    private boolean isSent;

    @Email
    private String createdByEmail;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String branchCode;

}
