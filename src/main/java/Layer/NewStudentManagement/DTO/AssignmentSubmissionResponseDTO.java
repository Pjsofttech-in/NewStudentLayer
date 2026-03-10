package Layer.NewStudentManagement.DTO;


import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentSubmissionResponseDTO
{
    private Long id;
    private String fileUrl;
    private String remarks;
    private LocalDate submittedDate;
    private String status;
    private Long studentId;
    private String studentName;
    private Integer rollNo;
    private Long assignmentId;
    private String assignmentTitle;
    private String createdByEmail;
    private String role;
    private String branchCode;

}
