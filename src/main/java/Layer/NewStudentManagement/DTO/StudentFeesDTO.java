package Layer.NewStudentManagement.DTO;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentFeesDTO
{
    private Long fid;
    private String studentName;
    private Integer rollNo;
    private String standardName;
    private String mediumName;
    private String feesType;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate approvalDate;

    private String feesStatus;
    private String feesCollectionType;

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
    private Double feesAmount;      // fees total before discount

    private double discount;
    private double discountedAmount;
    private Double totalamount;         // fees total after discount
    private double lateFeeCharges;
    private Long sfid;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

}
