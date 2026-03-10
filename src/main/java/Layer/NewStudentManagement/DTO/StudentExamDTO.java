package Layer.NewStudentManagement.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentExamDTO
{
    private Long id;
    private String examName;
    private LocalDate examDate;
    private String examType;
    private String createdByEmail;
    private String role;
    private String branchCode;
    private Long classRoomId;
    private List<ExamSubjectDto> subjects;
}
