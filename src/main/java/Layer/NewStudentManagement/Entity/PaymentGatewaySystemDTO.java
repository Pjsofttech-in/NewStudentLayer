package Layer.NewStudentManagement.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentGatewaySystemDTO {
    private Long id;
    private String systemName;
    private Boolean active;
}

