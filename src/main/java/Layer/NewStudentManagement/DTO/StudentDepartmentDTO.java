package Layer.NewStudentManagement.DTO;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentDepartmentDTO
{
    private Long id;
    private String departmentName;
    private Long degreeNameId;
    private String degreeName;
    private String createdByEmail;
    private String role;
    private String branchCode;
}
