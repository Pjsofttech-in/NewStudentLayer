package Layer.NewStudentManagement.DTO;

import Layer.NewStudentManagement.Entity.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudentRequest
{

    private Long standardId;
    private Long mediumId;
    private Long streamId;
    private Long graduationTypeId;
    private Long degreeNameId;
    private Long departmentId;
    private StudentEntity student;
    private StudentAddress address;
    private List<StudentEducation> educationList;
    private StudentAdditionalInfo additionalInfo;
    private StudentReligion religion;
    private StudentSports sports;
}