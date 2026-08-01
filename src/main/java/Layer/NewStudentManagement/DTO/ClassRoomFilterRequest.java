package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassRoomFilterRequest
{
    private String institutionType;
    private Long graduationTypeId;
    private Long streamId;
    private Long mediumId;
    private Long standardId;
    private Long degreeNameId;
    private Long certificationId;
    private String departmentName;
    private String groupName;
    private String year;

}
