package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionInfoDTO
{
    private Long standardId;
    private String standardName;
    private Long mediumId;
    private Integer rollNo;
    private String mediumName;
    private String academicYear;
    private String institutionType;
    private LocalDate promotionDate;
    private Boolean isCurrent;

    private Long classroomId;
    private String division;

    private Long studentId;
    private String studentFullName;
    private Long degreeId;
    private String degreeName;

    private Long departmentId;
    private String departmentName;

    private Long streamId;
    private String streamName;

    private String groupName;
}
