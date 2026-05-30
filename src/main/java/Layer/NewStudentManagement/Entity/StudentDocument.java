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
public class StudentDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String studentPhoto;
    private String aadharcardPhoto;
    private String pancardPhoto;
    private String casteValidationPhoto;
    private String casteCertificatePhoto;
    private String leavingCertificatePhoto;
    private String domicilePhoto;
    private String birthCertificatePhoto;
    private String disabilityCertificate;
    private String studentSignPhoto;

    // --- New Document Upload S3 URL Columns ---
    private String marksheet10thCert;
    private String marksheet12thCert;
    private String graduationMarksheetCert;
    private String nonCreamyLayerCert;
    private String incomeCertificateCert;

    // --- Generate Getters and Setters for these 6 fields ---

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

}
