package Layer.NewStudentManagement.DTO;

import lombok.Data;

@Data
public class FeeReminderDTO {
    private String dueDateStr;
    private Double collectAmount;
    private String studentName;
    private String studentPhoneNo;
}
