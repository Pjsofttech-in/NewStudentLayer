package Layer.NewStudentManagement.DTO;

import Layer.NewStudentManagement.Entity.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequest
{

    private Long standardId;
    private Long mediumId;
    private StudentEntity student;
    private StudentAddress address;
    private List<StudentEducation> educationList;
    private StudentAdditionalInfo additionalInfo;
    private StudentReligion religion;
    private StudentSports sports;
}