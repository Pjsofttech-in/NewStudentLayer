package Layer.NewStudentManagement.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RazorPayOrderCreationDTO {
    @NotBlank
    String role;
    @Email
    String email;
    @Positive
    BigDecimal amount;
    @NotNull
    Long studentFeeScheduleId;
}
