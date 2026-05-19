package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeesScheduleChartByYearDTO {
    private String year;
    private double paidAmount;
    private double totalAmount = 0;
    private double pendingAmount;

    // Constructor, Getters, and Setters
    public FeesScheduleChartByYearDTO(String year) {
        this.year = year;
    }

    public void calculateTotalAmount() {
        this.totalAmount = this.getPaidAmount() + this.getPendingAmount();
    }
}