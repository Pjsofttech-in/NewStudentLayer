package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentResultDetailDTO
{
    private Long subjectId;
    private String subjectName;
    private Integer maxMarks;
    private Integer passingMarks;
    private Integer obtainedMarks;
    private String status;
}
