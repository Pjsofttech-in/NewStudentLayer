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
public class StudentTeacherDTO
{
    private Long id;
    private String teacherName;
    private String teacherEmail;
    private String institutionType;
    private Long graduationTypeId;
    private String graduationType;
    private Long streamId;
    private String stream;
    private String branchCode;
    private String role;
    private String createdByEmail;
    private List<StudentSubjectDTO> subjects;
}
