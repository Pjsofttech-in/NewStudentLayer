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
public class TimeTableRequestDTO
{
    private Long timeTableId;
    private String dayOfWeek;
    private Long classRoomId;
    private List<ScheduledPeriodRequestDTO> scheduledPeriods;

}
