package Layer.NewStudentManagement.DTO;

import Layer.NewStudentManagement.Enum.Role;
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
    private String institutionType;
    private String streamName;
    private String graduationTypeName;
    private String degreeName;
    private String departmentName;
    private String groupName;
    private String academicYear;
    private String createdByEmail;
    private Role role;
    private String branchCode;
}
