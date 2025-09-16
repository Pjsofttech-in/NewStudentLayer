package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeTableResponceDTO
{
    private Long id;
    private String dayOfWeek;
    private Long classRoomId;
    private List<StudentPeriodResponseDTO> periods;
    private String createdByEmail;
    private String branchCode;
    private String role;
}
