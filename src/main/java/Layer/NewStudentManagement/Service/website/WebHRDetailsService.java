package Layer.NewStudentManagement.Service.website;

import Layer.NewStudentManagement.Entity.website.StudentWebHRDetails;

import java.util.List;

public interface WebHRDetailsService {
    StudentWebHRDetails create(StudentWebHRDetails webHRDetails, String role, String email, String url);
    List<StudentWebHRDetails> getAllByBranchCode(String role, String email, String url, String branchCode);
    StudentWebHRDetails getById(Long id, String role, String email, String url);
    StudentWebHRDetails update(Long id, StudentWebHRDetails webHRDetails, String role, String email, String url);
    void delete(Long id, String role, String email, String url);
}
