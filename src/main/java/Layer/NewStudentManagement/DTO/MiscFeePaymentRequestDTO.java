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
public class MiscFeePaymentRequestDTO {
    private Double amountPaid;
    private String paymentMode; // "CASH", "UPI", "ONLINE", "CHEQUE"
    private String transactionId; // Optional: if UPI or Online
    private LocalDate paymentDate;
    private String bankName; // Optional: if Cheque

    private String ifscCode;
    private String bankBranchName;
    private String accHolderName;
}