package Layer.NewStudentManagement.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {
    private Long id;
    private String title;
    private String fullName;
    private String gender;
    private String bloodGroup;
    private String motherTongue;
    private String maritalStatus;
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String contact;
    private Long age;
    @Column(unique = true, nullable = false)
    private String email;
    private LocalDate dateOfBirth;
    private String birthPlace;
    private String birthCountry;
    private String pancardNumber;
    private Long aadharNumber;
    private Integer rollNo;
    private String standardName;
    @Column(length = 9)
    private String academicYear;
    private String udiseNo;
    private String apaarId;
    private String mediumName;
    private LocalDate enrollmentDate;
    private LocalDate approvalDate;
    private String status;
    private String applyFor;
    private String streamName;
    private String groupName;
    private String semister;
    private String institutionType;
    private Long standardId;
    private Long mediumId;
    private Long streamId;
    private Long graduationTypeId;
    private String graduationType;
    private Long courseTypeId;
    private String courseType;
    private Long degreeNameId;
    private String degreeName;
    private String fatherEmailId;

    private Long certificationId;
    private String certificationName;

    private Double scholarshipAmount;
    private String admissionType;
    private String departmentName;
    private String password;
    private String registrationNumber;
    private String generalRegistrationNumber;
    private String formStatus;
    private String reason;
    //    private Double discount;
    private Long classsRoomId;
    private boolean isTcGenrated;
    private String oldRegisterPhoto;
    private String applicationNumber;
    private boolean entranceExam;
    private String entranceExamName;
    private Integer entranceMarks;
    private Integer eMarksOutOff;
    private String entranceMarkSheet;
    private String permanentEducationNumber;
    private String abcId;
    private String enrollmentNumber;
    private String dteNumber;


    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

    private StudentAddressDTO address;
    private List<StudentEducationDTO> educationList;
    private StudentAdditionalInfoDTO additionalInfo;
    private StudentReligionDTO religion;
    private StudentSportsDTO sports;
    private StudentDocumentDTO documents;

    private StudentCollegeDetailsDTO collegeDetailsDTO;

}
