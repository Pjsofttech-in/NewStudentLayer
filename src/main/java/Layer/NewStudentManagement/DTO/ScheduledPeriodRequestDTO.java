package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduledPeriodRequestDTO {
    private Long id;
    private Long periodSlotId;
    private Long teacherId;
    private Long subjectId;
}
