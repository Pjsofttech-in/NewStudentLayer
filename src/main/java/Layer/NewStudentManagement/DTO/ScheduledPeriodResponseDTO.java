package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class ScheduledPeriodResponseDTO
{
    private Long id;

    private Long periodSlotId;
    private Integer periodNo;
    private String startTime;
    private String endTime;

    private Long teacherId;
    private String teacherName;

    private LocalDate periodDate;  // add this
    private String status;

    private Long subjectId;
    private String subjectName;
}
