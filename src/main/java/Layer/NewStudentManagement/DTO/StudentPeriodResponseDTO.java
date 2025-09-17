package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentPeriodResponseDTO
{
    private Long id;
    private int periodNo;
    private String startTime;
    private String endTime;

    private String createdByEmail;
    private String branchCode;
    private String role;
}
