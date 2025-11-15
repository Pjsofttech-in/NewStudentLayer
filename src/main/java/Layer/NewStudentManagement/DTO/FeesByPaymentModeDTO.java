package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
public class FeesByPaymentModeDTO
{
    private String paymentMode;
    private Double totalAmount;

    public FeesByPaymentModeDTO(String paymentMode, BigDecimal totalAmount) {
        this.paymentMode = paymentMode;
        this.totalAmount = (totalAmount != null) ? totalAmount.doubleValue() : 0.0;
    }
}
