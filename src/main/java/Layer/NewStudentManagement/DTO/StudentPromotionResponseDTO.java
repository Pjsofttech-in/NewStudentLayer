package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentPromotionResponseDTO
{
    private Long studentId;
    private String fullName;
    private Integer rollNo;
    private String branchCode;

    private PromotionInfoDTO currentPromotion;
    private List<PromotionInfoDTO> promotionHistory;
}
