package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeesScheduleChartDTO {
    private String monthName;
    private double paidAmount;
    private double totalAmount = 0;
    private double pendingAmount;

    // Constructor, Getters, and Setters
    public FeesScheduleChartDTO(String monthName) {
        this.monthName = monthName;
    }

    public void setPendingAmount(Double pendingAmount) {
        this.pendingAmount = pendingAmount;
        if (totalAmount == 0)
            totalAmount = paidAmount + pendingAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
        if (totalAmount == 0)
            totalAmount = paidAmount + pendingAmount;
    }
}