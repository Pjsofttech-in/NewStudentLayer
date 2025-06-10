package Layer.NewStudentManagement.DTO;

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
    private String medium;
    private String createdByEmail;
    private String role;
    private String branchCode;

}
