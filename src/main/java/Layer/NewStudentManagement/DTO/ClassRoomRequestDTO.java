package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassRoomRequestDTO
{
    private String year;
    private Long mediumId;
    private Long divisionId;
    private Long standardId;
    private Map<Long, List<Long>> teacherSubjectMap;
}
