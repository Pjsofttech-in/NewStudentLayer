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
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDTO {
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
    private Long standardId;
    private Long mediumId;
    private Long streamId;
    private Long graduationTypeId;
    private String graduationType;
    private Long courseTypeId;
    private String courseType;
    private Long degreeNameId;
    private String degreeName;
    private Long certificationId;
    private String certificationName;

    private String departmentName;
    private String institutionType;
    private boolean earthquake;
    private boolean minority;
    private String castCategory;
    private boolean handicap;
    private String registrationNumber;
    private String generalRegistrationNumber;
    private String formStatus;
    private String reason;
    private Double discount;
    private boolean isTcGenrated;
    private Long classsRoomId;
    private String applicationNumber;
    private boolean scholarship;
    private boolean projectDifferentiated;
    private Boolean sportYesNo;
    private String createdByName;
    private String oldRegisterPhoto;

    private String fatherEmailId;

    private boolean entranceExam;
    private String entranceExamName;
    private Integer entranceMarks;
    private Integer eMarksOutOff;
    private String entranceMarkSheet;

    private Double scholarshipAmount;
    private String admissionType;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

    private StudentCollegeDetailsDTO collegeDetailsDTO;
    private StudentDocumentDTO documentDTO;
    private StudentAddressDTO addressDTO;
}
