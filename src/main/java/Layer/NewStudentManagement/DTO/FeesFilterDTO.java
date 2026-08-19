package Layer.NewStudentManagement.DTO;


import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeesFilterDTO {
    private String studentName;
    private String standardName;
    private String mediumName;
    private String streamName;
    private String courseType;
    private String graduationTypeName;
    private String degreeName;
    private String departmentName;
    private String miscFeeComponentName;
    private String groupName;
    private String institutionType;
    private String academicYear;
    private String feesCollectionType;
    private String feesStatus;
    @Email
    private String createdByEmail;
    private String createdByName;
    private String dueDate;

    private String bankAccountName;
    private String paymentMode;
}
