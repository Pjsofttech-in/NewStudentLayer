package Layer.NewStudentManagement.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EditInstallmentRequest {
    private Double newAmount;
    private LocalDate dueDate;
    private String month; // e.g., "Updated 1st Installment"
}