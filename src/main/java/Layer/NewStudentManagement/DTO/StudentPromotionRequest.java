package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentPromotionRequest
{
    private Long studentId;
    private Long newStandardId;
    private Long newMediumId;
    private Long newDegreeNameId;
    private Long newDepartmentId;
    private Long newStreamId;
    private String groupName;
    private String academicYear;
    private Long newClassroomId;
    private String institutionType;
    private Long graduationTypeId;
}
