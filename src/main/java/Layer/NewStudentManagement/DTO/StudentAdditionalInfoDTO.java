package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentAdditionalInfoDTO
{
    private Long id;
    private boolean handicap;
    private boolean earthquake;
    private Long earthquakeNumber;
    private boolean projectDifferentiated;
    private Long projectDifferentiatedNumber;
    private boolean ebc;
    private boolean scholarship;
    private String scholarshipName;
    private String specialPercentage;
    private String disabilityType;
    private boolean isTcGenrated;
}