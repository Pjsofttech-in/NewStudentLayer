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
    private String standard;
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
//    private String university;
//    private String board;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

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
    @JoinColumn(name = "classroom_id")
    private StudentClassRoom classRoom;

}
