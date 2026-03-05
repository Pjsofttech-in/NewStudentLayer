package Layer.NewStudentManagement.DTO;

import Layer.NewStudentManagement.Enum.Role;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StreamDTO
{

    private Long id;
    private String stream;
    private String createdByEmail;
    private Role role;
    private String branchCode;
}
