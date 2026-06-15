package Layer.NewStudentManagement.DTO;


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
    private String degreeName;
    private String departmentName;
    private String courseType;

    private Long mediumId;
    private Long divisionId;
    private Long standardId;
    private Long courseTypeId;

    private Long graduationTypeId;
    private Long streamId;
    private Long degreeNameId;
    private String branchCode;
    private String email;
    private String role;
    private List<TeacherWithSubjectsDTO> teacherSubjectMappings;



}