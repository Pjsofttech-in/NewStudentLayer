package Layer.NewStudentManagement.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentClassRoomFilterDTO
{

    private String institutionType;
    private String standard;
    private String medium;
    private String academicYear;

    // Jr. College
    private String streamName;
    private String groupName;
    private String graduationType;
    private String courseType;


    private String degreeName;
    private String departmentName;

    private String status;
}
