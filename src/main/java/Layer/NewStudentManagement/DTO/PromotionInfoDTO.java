package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionInfoDTO
{
    private Long standardId;
    private String standardName;
    private Long mediumId;
    private String mediumName;
    private String academicYear;
    private LocalDate promotionDate;
    private Boolean isCurrent;
}
