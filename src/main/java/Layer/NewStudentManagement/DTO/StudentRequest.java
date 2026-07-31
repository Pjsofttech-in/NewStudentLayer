package Layer.NewStudentManagement.DTO;

import Layer.NewStudentManagement.Entity.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudentRequest
{

    private Long standardId;
    private Long courseTypeId;
    private Long mediumId;
    private Long streamId;
    private Long graduationTypeId;
    private Long degreeNameId;
    private Long certificationId;
    private String departmentName;

    private boolean entranceExam;
    private String entranceExamName;
    private Integer entranceMarks;
    private Integer eMarksOutOff;
    private String entranceMarkSheet;

    private StudentEntity student;
    private StudentCollegeDetails collegeDetails;
    private StudentAddress address;
    private List<StudentEducation> educationList;
    private StudentAdditionalInfo additionalInfo;
    private StudentReligion religion;
    private StudentSports sports;
}