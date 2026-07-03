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
public class AssignmentResponseDTO
{
    private Long id;
    private String assignmentTitle;
    private String description;
    private LocalDate dueDate;
    private LocalDate createdDate;
    private String image;
    private Long classRoomId;
    private String createdByEmail;
    private String role;
    private String branchCode;
    private String assignmentStatus;
}
