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
public class StudentDegreeNameDTO
{
    private Long id;
    private String degreeName;
    private Long graduationTypeId;
    private String graduationType;
    private String createdByEmail;
    private String role;
    private String branchCode;
}
