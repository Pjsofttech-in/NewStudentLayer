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
public class StudentAdditionalInfo
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean handicap;
//    private String handicapCertificate;
    private boolean earthquake;
    private Long earthquakeNumber;
    private boolean projectDifferentiated;
    private Long projectDifferentiatedNumber;
    private boolean ebc;
    private boolean scholarship;
    private String scholarshipName;
    private String specialPercentage;
    private String disabilityType;

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

}
