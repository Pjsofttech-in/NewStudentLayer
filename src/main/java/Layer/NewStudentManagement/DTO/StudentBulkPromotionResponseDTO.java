package Layer.NewStudentManagement.DTO;

import lombok.*;

import java.util.List;

@Data
public class StudentBulkPromotionResponseDTO {
    private String status;
    private List<Long> failedStudentIds;
    private List<StudentPromotionResponseDTO> successfulStudentList;
}
