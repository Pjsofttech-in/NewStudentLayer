package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudentClassRoomResponseDTO {
    private Long id;
    private String year;
    private String className;
    private String medium;
    private String division;
    private String standard;
    private String branchCode;
    private String email;
    private String role;
}