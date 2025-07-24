package Layer.NewStudentManagement.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeesFilterDTO
{
    private String standardName;
    private String mediumName;
    private String streamName;
    private String graduationTypeName;
    private String degreeName;
    private String departmentName;
    private String groupName;
    private String institutionType;
    private String academicYear;
    private String feesCollectionType;
    private String feesStatus;
}
