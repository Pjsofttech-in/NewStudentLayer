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
public class ClassRoomRequestDTO
{
    private String year;
    private LocalTime startTime;
    private LocalTime endTime;

    private Long mediumId;
    private Long divisionId;

    private String departmentName; // for College

    private Long standardId;
    private Long courseTypeId;
    private Long graduationTypeId;

    private Long streamId;
    private String groupName; // for Jr. College

    private Long degreeNameId;

    private Long certificationId;

    private Map<Long, List<Long>> teacherSubjectMap; // teacherId -> List<subjectId>

    private String institutionType; // "School", "College"
}
