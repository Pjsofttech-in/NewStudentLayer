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
public class SchoolBankDTO
{
    private Long id;
    private String bankName;
    private String bankBranchName;  // Branch Name
    private String ifscCode;        // IFSC Code
    private String accountHolderName;       // Bank Account Name

    private String createdByEmail;
    private String branchCode;
    private String role;
    private Long schoolProfileId;

}
