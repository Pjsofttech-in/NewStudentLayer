package Layer.NewStudentManagement.DTO;

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
public class FeesCollectDTO
{
    private Long id;
    private Double amount;
    private String invoice;
    private LocalDate duedate;
    private LocalDate paymentDate;
    private String paymentMode; // Cash, Online, UPI
    private String feesPaymentType;
    private String status;
    private String transactionId;

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

    private Long studentFeesId;
    private StudentFeeScheduleDTO schedule;



}
