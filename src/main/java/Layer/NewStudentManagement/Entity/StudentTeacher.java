package Layer.NewStudentManagement.Entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentTeacher
{
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String teacherName;
    @Column(unique = true)
    private String teacherEmail;
    private String password;
    private String gender;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate joiningDate;
    private String institutionType;
    private String streamName;
    private String otp;
    private Long otpRequestedTime;
    private String graduationTypeName;
    private String degreeName;
    private String departmentName;
    private String profilePhoto;
    private String education;
    private String experience;
    private String reserch;

    private boolean active = true;

    @Email
    private String createdByEmail;

    private String role;
    private String branchCode;
    @ManyToMany
    @JoinTable(
            name = "student_teacher_subject",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private List<StudentSubject> subjects;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private List<StudentClassRoomTeacherSubject> assignments;

    @ManyToOne
    @JoinColumn(name = "graduation_type_id", nullable = true)
    private StudentGraduationType graduationType;

    @ManyToOne
    @JoinColumn(name = "course_type_id", nullable = true)
    private StudentCourseType courseType;

    @ManyToOne
    @JoinColumn(name = "certification_id", nullable = true)
    private StudentCertification certification;

    @ManyToOne
    @JoinColumn(name = "stream_id", nullable = true)
    private StudentStream stream;

    @ManyToOne
    @JoinColumn(name = "degree_id", nullable = true)
    private StudentDegreeName degree;
}
