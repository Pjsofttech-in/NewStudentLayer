package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BonafideRequestDTO {

    private Long id;
    private Long studentId;
    
    // Read-only fields to display to staff in the UI
    private String studentName;
    private String standardName;
    private Integer rollNo;
    
    private String reason;
    private LocalDate requestDate;
    private String status;
    private String remarks;
    
    private String processedByEmail;
    private LocalDate processedDate;
    private String branchCode;

    private String createdByEmail; // Which staff member approved/rejected it
    private String createdByRole;
    private LocalDate createdByDate;

    private String updatedByEmail; // Which staff member approved/rejected it
    private String updatedByRole;
    private LocalDate updatedByDate;
}