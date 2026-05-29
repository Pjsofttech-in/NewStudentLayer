package Layer.NewStudentManagement.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RazorPayVerifyPaymentDTO {
    @NotBlank
    String role;
    @Email
    String email;
    @NotBlank
    String razorpay_order_id;
    @NotBlank
    String razorpay_payment_id;
    @NotBlank
    String razorpay_signature;
}
