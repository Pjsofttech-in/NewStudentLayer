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
public class StudentRegisterRequest
{
    private String title;
    private String fullName;
    private String gender;
    private String contact;
    private String email;
    private String password;
    private LocalDate dateOfBirth;
    private String status;
    private String standardName;
    private String mediumName;
    private String streamName;
    private String groupName;
    private String semister;
    private String institutionType;
    private String academicYear;
    private Double discount;

    private String formStatus ;
    private Long standardId;
    private Long mediumId;
    private Long streamId;
    private Long graduationTypeId;
    private Long degreeNameId;
    private Long departmentId;
    private String oldRegisterPhoto;

    private boolean entranceExam;
    private String entranceExamName;
    private Integer entranceMarks;
    private Integer eMarksOutOff;
    private String entranceMarkSheet;

    private String createdByEmail;
    private String role;
    private String branchCode;
}
