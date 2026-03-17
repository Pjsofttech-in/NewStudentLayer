package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentClassRoomRequestDTO
{
    private String institutionType;
    private String year;

    private LocalTime startTime;
    private LocalTime endTime;

    private Long mediumId;
    private Long divisionId;
    private Long standardId;

    private Long graduationTypeId;
    private Long streamId;
    private Long degreeNameId;
    private Long departmentId;

    private String groupName;

    // 🔥 MAIN FIELD
    private Map<Long, List<Long>> teacherSubjectMap;
}
