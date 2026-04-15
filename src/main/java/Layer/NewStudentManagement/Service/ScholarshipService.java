package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.Entity.StudentScholarship;
import jakarta.annotation.Nullable;

import java.util.List;

public interface ScholarshipService
{

    StudentScholarship createScholarship(String role, String email, StudentScholarship scholarship);

    StudentScholarship getScholarshipById(Long id, String role, String email);

    List<StudentScholarship> getAllScholarships(String role, String email, String branchCode);

    StudentScholarship updateScholarship(Long id, String role, String email, StudentScholarship scholarship);

    void deleteScholarship(Long id, String role, String email);
}
