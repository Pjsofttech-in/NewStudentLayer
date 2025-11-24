package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentResultDTO
{

    private Long id;
    private Long studentId;
    private String studentName;
    private Long examId;
    private String examName;
    private String examType;
    private Integer rollNo;
    private String division;
    private String overAllStatus;
    private Integer totalObtained;
    private Integer totalMax;
    private Double percentage;
    private List<StudentResultDetailDTO> details;
}
