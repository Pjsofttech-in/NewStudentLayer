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
public class FeesCollectionDetailDTO
{
    private String accountHolderName;
    private Double amount;
    private LocalDate paymentDate;
    private String paymentMode;
    private String feesType;
    private String month;
    private String transactionId;
}
