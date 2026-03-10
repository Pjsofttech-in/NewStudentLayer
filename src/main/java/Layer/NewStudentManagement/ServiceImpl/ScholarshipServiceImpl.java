package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentScholarship;

import Layer.NewStudentManagement.Repository.ScholarshipRepository;
import Layer.NewStudentManagement.Service.ScholarshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScholarshipServiceImpl implements ScholarshipService
{

    @Autowired
    ScholarshipRepository scholarshipRepository;

    @Autowired
    StaffService staffService;

    
    @Override
    public StudentScholarship createScholarship(String role, String email, StudentScholarship scholarship)
    {

        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create scholarship");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role,email);

        if(scholarshipRepository.existsScholarship(
                scholarship.getScholarshipName(),branchCode))
        {
            throw new RuntimeException("Scholarship already exists for this branch");
        }

        scholarship.setBranchCode(branchCode);
        scholarship.setRole(role);
        scholarship.setCreatedByEmail(email);

        return scholarshipRepository.save(scholarship);
    }

    @Override
    public StudentScholarship getScholarshipById(Long id, String role, String email)
    {

        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get scholarship");
        }

        StudentScholarship scholarship = scholarshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scholarship not found"));

        return scholarship;
    }

    @Override
    public List<StudentScholarship> getAllScholarships(String role, String email)
    {

        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get scholarships");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role,email);

        List<StudentScholarship> scholarships = scholarshipRepository.findAllByBranchCode(branchCode);

        return scholarships;
    }

    @Override
    public StudentScholarship updateScholarship(Long id, String role, String email, StudentScholarship scholarship)
    {

        if(!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to update scholarship");
        }

        StudentScholarship existing = scholarshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scholarship not found"));

        existing.setScholarshipName(scholarship.getScholarshipName());

        return scholarshipRepository.save(existing);
    }

    @Override
    public void deleteScholarship(Long id, String role, String email)
    {

        if(!staffService.hasPermission(role,email,"Delete"))
        {
            throw new RuntimeException("You don't have permission to delete scholarship");
        }

        StudentScholarship scholarship = scholarshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scholarship not found"));

        scholarshipRepository.delete(scholarship);
    }


}
