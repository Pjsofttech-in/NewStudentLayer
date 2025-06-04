package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentDocumentDTO
{
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
    private Long studentId;


}
