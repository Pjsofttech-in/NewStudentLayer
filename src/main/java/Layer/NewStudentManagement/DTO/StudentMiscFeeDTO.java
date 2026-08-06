package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentMiscFeeDTO {
    private Long id;
    private Long studentFeesId;    // The academic year fee record this belongs to
    private Long feeComponentId;   // What kind of fee it is (from master list)
    private String componentName;  // For display purposes in GET requests
    
    private Double amount;
    private Double paidAmount;
    private Double pendingAmount;
    private LocalDate dueDate;
    private String status;
    private String description;
    
    private String createdByEmail;
    private String branchCode;
    private LocalDate assignedDate;
}