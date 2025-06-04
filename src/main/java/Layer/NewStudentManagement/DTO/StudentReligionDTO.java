package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentReligionDTO
{
    private Long id;
    private String religion;
    private boolean minority;
    private String castCategory;
    private String minorityType;
    private String casteCertificateNumber;
    private Boolean casteValidation;
    private String casteValidationNumber;
    private String subCaste;
}