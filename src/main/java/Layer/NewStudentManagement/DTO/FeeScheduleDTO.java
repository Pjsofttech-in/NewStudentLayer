package Layer.NewStudentManagement.DTO;

import lombok.Data;

@Data
public class FeeScheduleDTO {
    private Long id;
    private String FeesType;    // "Monthly" or "Installment"
    private String month;// "July", "1st Installment", etc.
    private boolean isPaid;
    private Double collectAmount;
}
