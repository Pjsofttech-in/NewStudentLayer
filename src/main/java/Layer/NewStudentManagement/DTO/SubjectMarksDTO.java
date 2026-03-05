package Layer.NewStudentManagement.DTO;

import Layer.NewStudentManagement.Enum.Role;
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
    private Integer passingMarks;
    private Long classRoomId;
    private String createdByEmail;
    private Role role;
    private String branchCode;
}
