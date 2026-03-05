package Layer.NewStudentManagement.Entity;

import Layer.NewStudentManagement.Enum.Role;
import jakarta.persistence.*;
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
@Entity
public class StudentFeesCollect
{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private Double amount;
        private String invoice;
        private LocalDate duedate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate paymentDate;
        private String paymentMode; // Cash, Online, UPI
        private String feesPaymentType;
        private String status;
        private String transactionId;
        private String bankName;        // Bank Name
        private String bankBranchName;  // Branch Name
        private String ifscCode;        // IFSC Code
        private String accountHolderName;       // Bank Account Name

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
        private String feesType; // e.g., "July", "1st Installment"
        private String month;

        @ManyToOne
        @JoinColumn(name = "student_fees_id")
        private StudentFees studentFees;
        @ManyToOne
        @JoinColumn(name = "schedule_id")
        private StudentFeeSchedule studentFeeSchedule;

        @Email
        private String createdByEmail;
        private String branchCode;
        @Enumerated(EnumType.STRING)
        private Role role;
}
