package Layer.NewStudentManagement.Service.website;

import Layer.NewStudentManagement.Entity.website.StudentWebSecurityUrl;

import java.util.List;

public interface SecurityUrlService {
    StudentWebSecurityUrl create(StudentWebSecurityUrl webSecurityUrl, String role, String email); // No permission
    List<StudentWebSecurityUrl> getAllByBranchCode(String role, String email, String branchCode);
    StudentWebSecurityUrl update(long id, StudentWebSecurityUrl webSecurityUrl, String role, String email);
    String getBranchCodeByUrl(String url);

}