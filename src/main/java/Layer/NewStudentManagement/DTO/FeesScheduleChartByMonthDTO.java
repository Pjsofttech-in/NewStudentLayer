package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeesScheduleChartByMonthDTO {
    private String monthName;
    private double paidAmount;
    private double totalAmount = 0;
    private double pendingAmount;

    // Constructor, Getters, and Setters
    public FeesScheduleChartByMonthDTO(String monthName) {
        this.monthName = monthName;
    }

    public void calculateTotalAmount() {
        this.totalAmount = this.getPaidAmount() + this.getPendingAmount();
    }
}