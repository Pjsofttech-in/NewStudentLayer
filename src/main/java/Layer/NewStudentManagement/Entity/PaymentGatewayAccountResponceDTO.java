package Layer.NewStudentManagement.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentGatewayAccountResponceDTO {
    private Long id;
    private String instituteEmail;
    private String branchCode;
    private String gatewayName; // RAZORPAY
    private String keyId;
    private String secretKey;
    private String bankName;
    private String ifscCode;
    private String bankBranchName;
    private String accountHolderName;
    private Long branchId;
    private List<PaymentGatewaySystemDTO> systems;
}
