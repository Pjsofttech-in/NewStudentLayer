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
public class StudentPromotionRequest
{
    private Long studentId;
    private List<Long> bulkStudentIds;
    private Long newStandardId;
    private Long newMediumId;
    private Long newDegreeNameId;
    private String newDepartmentName;
    private Long newStreamId;
    private String groupName;
    private String academicYear;
    private Long newClassroomId;
    private String institutionType;
    private Long graduationTypeId;
}
