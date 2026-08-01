package Layer.NewStudentManagement.DTO;


import com.fasterxml.jackson.annotation.JsonFormat;
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
public class StudentTeacherDTO
{
    private Long id;
    private String teacherName;
    private String teacherEmail;
    private String institutionType;
    private Long graduationTypeId;
    private String graduationType;
    private Long courseTypeId;
    private String courseType;
    private Long streamId;
    private String stream;
    private Long certificationId;
    private String certification;
    private String degreeName;
    private Long degreeId;
    private String departmentName;
    private String profilePhoto;
    private String education;
    private String experience;
    private String gender;
    private String reserch;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate joiningDate;
    private String branchCode;
    private String role;
    private String createdByEmail;
    private List<StudentSubjectDTO> subjects;
}
