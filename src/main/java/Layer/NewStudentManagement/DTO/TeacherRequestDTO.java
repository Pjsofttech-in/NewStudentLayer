package Layer.NewStudentManagement.DTO;

import Layer.NewStudentManagement.Entity.StudentTeacher;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TeacherRequestDTO
{
    private String teacherName;
    private String teacherEmail;
    private String institutionType;

    private String password;
    private List<Long> subjectIds;

    private String createdByEmail;
    private String role;
    private String branchCode;
}