package Layer.NewStudentManagement.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FeeScheduleDTO {
    private Long id;
    private String FeesType;    // "Monthly" or "Installment"
    private String month;// "July", "1st Installment", etc.
    private boolean isPaid;
    private LocalDate dueDate;
    private Double collectAmount;
}
