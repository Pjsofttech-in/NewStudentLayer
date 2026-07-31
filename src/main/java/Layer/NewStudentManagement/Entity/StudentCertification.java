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
public class StudentCertification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String certification;
    @Email
    private String createdByEmail;

    private String role;
    private String branchCode;

    @ManyToOne
    @JoinColumn(name = "stream_id")
    private StudentStream studentStream;
}
