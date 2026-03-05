package Layer.NewStudentManagement.DTO;

import Layer.NewStudentManagement.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
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
    private String classRoomName;

    private String createdByEmail;
    private String branchCode;
    private Role role;

    private List<ScheduledPeriodResponseDTO> scheduledPeriods;
}
