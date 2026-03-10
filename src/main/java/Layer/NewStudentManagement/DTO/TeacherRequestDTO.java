package Layer.NewStudentManagement.DTO;

import Layer.NewStudentManagement.Entity.StudentTeacher;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TeacherRequestDTO
{
    private String teacherName;
    private String teacherEmail;
    private String institutionType;
    private Long graduationTypeId;
    private Long streamId;
    private Long degreeId;
    private Long departmentId;
    private String education;
    private String experience;
    private String reserch;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
    private String gender;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate joiningDate;
    private String password;
    private List<Long> subjectIds;

    private String createdByEmail;
    private String role;
    private String branchCode;
}