package Layer.NewStudentManagement.Entity;

import Layer.NewStudentManagement.Enum.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class StudentStream
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String stream;
    @Email
    private String createdByEmail;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String branchCode;

    @OneToMany(mappedBy = "stream", cascade = CascadeType.ALL)
    private List<StudentGraduationType> graduationTypes;


}
