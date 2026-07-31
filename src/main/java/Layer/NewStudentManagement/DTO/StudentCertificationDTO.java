package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentCertificationDTO {
    
    private Long id;
    
    // The name or description of the certification
    private String certification;
    
    private String createdByEmail;
    private String role;
    private String branchCode;
    
    // The ID of the associated StudentStream
    private Long streamId;
}