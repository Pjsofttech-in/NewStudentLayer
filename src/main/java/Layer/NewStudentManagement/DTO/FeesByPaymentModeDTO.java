package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FeesByPaymentModeDTO
{
    private String paymentMode;
    private Double totalAmount;
}
