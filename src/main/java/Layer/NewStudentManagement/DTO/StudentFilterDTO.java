package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentFilterDTO
{
    private String fullName;
    private String gender;
    private String motherTongue;
    private String standardName;
    private String academicYear;
    private String mediumName;
    private String status;
    private String streamName;
    private String groupName;
    private String semister;
}
