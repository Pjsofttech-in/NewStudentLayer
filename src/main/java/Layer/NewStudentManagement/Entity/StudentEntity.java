package Layer.NewStudentManagement.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Integer rollNo = 0;
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
    private String departmentName;          //for year
    private String semister;
    private String password;
    private String institutionType;
    private String formStatus ;
    private String reason;
    private Double scholarshipAmount;
    private String admissionType;
//    private Double discount;
    private String userRole;
    private boolean isTcGenrated = true;
    @Column(unique = true, length = 12)
    private String registrationNumber;
    @Column(name = "general_registration_number", unique = true, length = 50)
    private String generalRegistrationNumber;
    private String applicationNumber;
    private String oldRegisterPhoto;
//    private String university;
//    private String board;
    private String otp;
    private Long otpRequestedTime;

    private boolean entranceExam;
    private String entranceExamName;
    private Integer entranceMarks;
    private Integer eMarksOutOff;
    private String entranceMarkSheet;
    private String permanentEducationNumber;

    @Column(name = "father_email_id", length = 100)
    private String fatherEmailId;

    private String parentPassword;

    @Email
    private String createdByEmail;

    private String role;
    private String branchCode;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private StudentCollegeDetails collegeDetails;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL)
    private StudentAddress address;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<StudentEducation> educationList;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL)
    private StudentAdditionalInfo additionalInfo;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL)
    private StudentReligion religion;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL)
    private StudentSports sports;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL)
    private StudentDocument documents;

    @ManyToOne
    @JoinColumn(name = "medium_id")
    private StudentMedium medium;

    @ManyToOne
    @JoinColumn(name = "certification_id")
    private StudentCertification certification;

    @ManyToOne
    @JoinColumn(name = "standard_id")
    private StudentStandard standard;


    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private StudentClassRoom classRoom;

    @ManyToOne
    @JoinColumn(name = "stream_id")
    private StudentStream stream;

    @ManyToOne
    @JoinColumn(name = "graduation_type_id")
    private StudentGraduationType graduationType;

    @ManyToOne
    @JoinColumn(name = "degree_name_id")
    private StudentDegreeName degreeName;

    @ManyToOne
    @JoinColumn(name = "course_type_id")
    private StudentCourseType courseType;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentPromotionRecord> promotionRecords;

}
