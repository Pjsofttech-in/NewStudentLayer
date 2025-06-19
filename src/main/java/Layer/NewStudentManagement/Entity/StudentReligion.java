package Layer.NewStudentManagement.Entity;

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
public class StudentReligion
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String religion;
    private boolean minority;
    private String castCategory;
    private String minorityType;
    private String casteCertificateNumber;
    private Boolean casteValidation;
    private String casteValidationNumber;
    private String subCaste;
    private boolean domicileBool;
    private Long domicileNumber;

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

}
