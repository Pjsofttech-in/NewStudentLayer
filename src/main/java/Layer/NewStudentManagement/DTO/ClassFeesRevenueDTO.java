package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassFeesRevenueDTO
{

    private Long classId;
    private String division;
    private String standardName;
    private String degreeName;
    private String departmentName;
    private String paymentMode;
    private Double totalFeesRevenue;
    private String status;
}
