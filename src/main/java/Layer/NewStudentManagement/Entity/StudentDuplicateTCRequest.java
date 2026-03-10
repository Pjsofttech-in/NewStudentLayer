package Layer.NewStudentManagement.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
public class StudentDuplicateTCRequest
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentId;
    private String fullName;
    private String tcNumber;
    private LocalDate requestDate;
    private String status;
    private LocalDate approvedDate;
    private String reason;
    @Email
    private String createdByEmail;

    private String role;
    private String branchCode;
}
