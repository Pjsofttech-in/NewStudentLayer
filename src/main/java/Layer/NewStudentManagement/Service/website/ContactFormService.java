package Layer.NewStudentManagement.Service.website;


import Layer.NewStudentManagement.Entity.website.StudentWebContactForm;

import java.util.List;

public interface ContactFormService {
    StudentWebContactForm create(StudentWebContactForm webContactForm, String role, String email, String url, String branchCodeFromRequest);
    List<StudentWebContactForm> getAllByBranchCode(String role, String email, String url, String branchCode);
    StudentWebContactForm update(Long id, StudentWebContactForm webContactForm, String role, String email, String url);
    void delete(Long id, String role, String email, String url);
    StudentWebContactForm getById(Long id, String role, String email, String url);
}