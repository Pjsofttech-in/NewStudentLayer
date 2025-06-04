package Layer.NewStudentManagement.DTO;

import Layer.NewStudentManagement.Entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequest
{
    private StudentEntity student;
    private StudentAddress address;
    private List<StudentEducation> educationList;
    private StudentAdditionalInfo additionalInfo;
    private StudentReligion religion;
    private StudentSports sports;
}