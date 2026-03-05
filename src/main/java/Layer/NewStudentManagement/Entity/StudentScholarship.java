package Layer.NewStudentManagement.Entity;

import Layer.NewStudentManagement.Enum.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentScholarship
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String scholarshipName;
    private String createdByEmail;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String branchCode;
}
