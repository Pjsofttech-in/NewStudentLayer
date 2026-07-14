package Layer.NewStudentManagement.Service.website;

import Layer.NewStudentManagement.Entity.website.StudentWebFooter;

import java.util.List;

public interface FooterService {
    StudentWebFooter createFooter(StudentWebFooter webFooter, String role, String email, String url);
    List<StudentWebFooter> getAllFootersByBranchCode(String role, String email, String url, String branchCode);
    StudentWebFooter updateFooter(Long id, StudentWebFooter webFooter, String role, String email, String url);
    void deleteFooter(Long id, String role, String email, String url);
    StudentWebFooter getFooterById(Long id, String role, String email, String url);
}
