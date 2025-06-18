package Layer.NewStudentManagement.DTO;

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
    private String role;
    private String branchCode;
}
