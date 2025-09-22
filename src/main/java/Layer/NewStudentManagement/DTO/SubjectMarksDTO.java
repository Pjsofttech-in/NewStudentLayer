package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubjectMarksDTO
{
    private Long id;
    private String subjectName;
    private Integer maxMarks;
    private Long classRoomId;
    private String createdByEmail;
    private String role;
    private String branchCode;
}
