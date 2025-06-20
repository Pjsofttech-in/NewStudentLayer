package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentAcademicYear;
import Layer.NewStudentManagement.Repository.AcademicYearRepository;
import Layer.NewStudentManagement.Service.AcademicYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AcademicYearServiceImpl implements AcademicYearService
{
    @Autowired
    private AcademicYearRepository academicYearRepository;

    @Autowired
    private StaffService staffService;

    @Override
    public StudentAcademicYear createAcademicYear(String role, String email, StudentAcademicYear academicYear)
    {
        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create AcademicYear");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        academicYear.setBranchCode(branchCode);
        academicYear.setRole(role);
        academicYear.setCreatedByEmail(email);
        return academicYearRepository.save(academicYear);

    }

    @Override
    public StudentAcademicYear getAcademicYearById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to delete AcademicYear");
        }
        StudentAcademicYear academicYear = academicYearRepository.findById(id).orElseThrow(()->new RuntimeException("Year not found"));
        return academicYear;


    }

    @Override
    public StudentAcademicYear updateAcademicYear(Long id,String role,String email,StudentAcademicYear academicYear)
    {

        if(!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to update AcademicYear");
        }
        StudentAcademicYear existingAcademicYear = academicYearRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Stream not found"));
        existingAcademicYear.setAcademicYear(academicYear.getAcademicYear());
        return academicYearRepository.save(existingAcademicYear);

    }

    @Override
    public void deleteAcademicYearById(Long id,String role,String email)
    {
        if (!staffService.hasPermission(role,email,"Delete"))
            throw new RuntimeException("You don't have permission to delete AcademicYear");
        academicYearRepository.deleteById(id);
    }

    @Override
    public List<StudentAcademicYear> getAllAcademicYear(String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get AcademicYear");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role,email);
       return academicYearRepository.findAllByBranchCode(branchCode);

    }
}

