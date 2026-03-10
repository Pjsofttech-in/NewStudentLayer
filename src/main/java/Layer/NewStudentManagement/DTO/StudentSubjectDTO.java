package Layer.NewStudentManagement.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentSubjectDTO
{
    private Long id;
    private String subject;
    private String createdByEmail;
    private String role;
    private String branchCode;
    private String institutionType;
    private Long graduationTypeId;
    private String graduationType;
    private Long streamId;
    private String stream;
    private Long degreeId;
    private String degreeName;
    private Long departmentId;
    private String departmentName;
}
