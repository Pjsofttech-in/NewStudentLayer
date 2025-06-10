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
public class TeacherWithSubjectsDTO {
    private Long teacherId;
    private String teacherName;
    private String teacherEmail;
    private List<String> subjects;
}
