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
public class StudentGraduationTypeDTO
{
    private Long id;
    private String graduationType;
    private Long streamId;
    private String streamName;
    private String createdByEmail;
    private String role;
    private String branchCode;
}
