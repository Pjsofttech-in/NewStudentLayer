package Layer.NewStudentManagement.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentFeesHistoryDTO {
    private Long studentId;
    private String studentName;
    private Integer rollNo;
    private String standardName;
    private String mediumName;
    private String branchCode;
   private String institutionType;
    private Double totalPaidAmount;
    private Double pendingAmount;
    private Long streamId;
    private String streamName;

    private Long groupId;
    private String groupName;

    private Long degreeId;
    private String degreeName;


    private String departmentName;
    private String feesCollectionType;
    private List<FeesCollectionDetailDTO> paymentHistory;

    private String createdByEmail;
    private String createdByName;
    private String role;
}