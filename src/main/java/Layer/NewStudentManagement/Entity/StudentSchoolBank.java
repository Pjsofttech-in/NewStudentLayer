package Layer.NewStudentManagement.Entity;

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
public class StudentSchoolBank
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bankName;

    @Email
    private String createdByEmail;
    private String branchCode;
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_profile_id", nullable = false)
    private StudentSchoolProfile schoolProfile;

}
