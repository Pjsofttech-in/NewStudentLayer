package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubmitMarkRequest
{
    private Long studentId;
    private Long examId;
    private Long subjectId;
    private Integer obtainedMarks;
}
