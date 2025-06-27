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
    private Long standardId;
    private Long mediumId;
    private String academicYear;

    private Long streamId;
    private String groupName;
    private Long graduationTypeId;

    private Long degreeNameId;
    private Long departmentId;


    private String status;
}
