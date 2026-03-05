package Layer.NewStudentManagement.DTO;

import Layer.NewStudentManagement.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MediumDTO
{
    private Long mid;
    private String mediumName;
    private String createdByEmail;
    private Role role;
    private String branchCode;

}
