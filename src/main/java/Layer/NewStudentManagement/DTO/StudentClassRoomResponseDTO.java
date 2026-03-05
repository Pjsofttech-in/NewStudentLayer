package Layer.NewStudentManagement.DTO;

import Layer.NewStudentManagement.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudentClassRoomResponseDTO {
    private Long id;
    private String year;
    private String medium;
    private String division;
    private String standard;
    private LocalTime startTime;
    private LocalTime endTime;
    private String groupName;
    private String graduationType;
    private String institutionType;
    private String streamName;
    private String departmentName;
    private String degreeName;
    private String branchCode;
    private String email;
    private Role role;
    private List<TeacherWithSubjectsDTO> teacherSubjectMappings;



}