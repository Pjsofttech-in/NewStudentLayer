package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentEducationDTO
{
    private Long id;
    private String classGrade;
    private String lastYear;
    private String schoolName;
    private int totalMarks;
    private int obtainedMarks;
    private double cgpa;
    private double percentage;
    private String grade;
    private String preStandard;
    private String collegeName;
    private String passOutyear;
    private String city;
    private String examMode;
    private String preBoard;
    private String enterExamName;
    private String reasonOfLeavingSchool;
    private String enrollmentNumber;
    private String examBoard;
    private String examUniversity;
}
