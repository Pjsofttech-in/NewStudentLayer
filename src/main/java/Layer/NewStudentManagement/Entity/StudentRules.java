package Layer.NewStudentManagement.Entity;

import Layer.NewStudentManagement.Enum.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class StudentRules
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rules;

    @Email
    private String createdByEmail;
    private String branchCode;
    @Enumerated(EnumType.STRING)
    private Role role;

}
