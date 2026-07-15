package Layer.NewStudentManagement.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AddInstallmentRequest {
    private Double amount;
    private LocalDate dueDate;
    private String month;
    private String feesType; // "Monthly" or "Installment"
}