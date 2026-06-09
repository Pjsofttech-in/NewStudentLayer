package Layer.NewStudentManagement.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentCourseTypeDTO {
    private Long id;
    private Long graduationTypeId;
    private String courseType;
    private String createdByEmail;
    private String role;
    private String branchCode;
}
