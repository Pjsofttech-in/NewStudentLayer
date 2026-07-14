package Layer.NewStudentManagement.Service.website;

import Layer.NewStudentManagement.Entity.website.StudentWebCounter;

import java.util.List;

public interface CounterService {
    StudentWebCounter createCounter(StudentWebCounter webCounter, String role, String email, String url);
    List<StudentWebCounter> getAllByBranchCode(String role, String email, String url, String branchCode);
    StudentWebCounter updateCounter(Long id, StudentWebCounter webCounter, String role, String email, String url);
    void deleteCounter(Long id, String role, String email, String url);
    StudentWebCounter getCounterById(Long id, String role, String email, String url);
}