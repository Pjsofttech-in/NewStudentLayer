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
public class StudentGroupDTO
{
    private Long id;
    private String studentGroup;
    private String graduationTypeName;
    private Long graduationTypeId;
    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;
}
