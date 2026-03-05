package Layer.NewStudentManagement.DTO;

import Layer.NewStudentManagement.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentDivisionDTO
{
    private Long did;
    private String divisionName;
    private String createdByEmail;
    private Role role;
    private String branchCode;
}
