package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StandardFeesRequestDTO
{
    private Long sfid;
    private Long standardId;
    private Long mediumId;
    private double tuitionFee;
    private double admissionFee;
    private double practicalFee;
    private double computerClassFee;
    private double examFees;
    private double uniformFee;
    private double transportBusFee;
    private double hostelFee;
    private double buildingFundFee;
    private double libraryFees;
    private double sportFees;
    private double GST;
    private Double feesAmount;
    private String standardName;
    private String mediumName;
    private String createdByEmail;
    private String role;
    private String branchCode;
}
