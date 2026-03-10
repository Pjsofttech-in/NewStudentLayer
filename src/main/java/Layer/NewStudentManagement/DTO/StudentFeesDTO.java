package Layer.NewStudentManagement.DTO;


import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

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
    private String streamName;          // For Jr. College students
    private String groupName;
    private String degreeName;         // For UG/PG students
    private String departmentName;     // For UG/PG students
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate approvalDate;
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
    private double GST;
    private Double totalamount;         // fees total after discount
    private Long sfid;
    private double paidAmount;         // total paid by student
    private double pendingAmount;     // remaining amount
//    private String paymentStatus;     // Pending, Ongoing, Completed
//    private String feesPaymentType;
//    private double lateFeeCharges;
    private String feesStatus;
    private String feesCollectionType;
    private String institutionType;
    private List<FeeScheduleDTO> scheduleList;

    private Long studentId;


    //    private String feesType;
    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

}
