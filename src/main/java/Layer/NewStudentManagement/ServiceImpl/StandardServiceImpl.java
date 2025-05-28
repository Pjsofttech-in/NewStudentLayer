package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentStandard;
import Layer.NewStudentManagement.Repository.StandardRepository;
import Layer.NewStudentManagement.Service.StandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StandardServiceImpl implements StandardService
{

    @Autowired
    private StandardRepository standardRepository;

    @Autowired
    private StaffService staffService;

    @Override
    public StudentStandard createStandard(String role, String email, StudentStandard standard)
    {
        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create standard");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        standard.setBranchCode(branchCode);
        standard.setRole(role);
        standard.setCreatedByEmail(email);

        return standardRepository.save(standard);
    }

    @Override
    public StudentStandard getStandardById(Long id,String role,String email){
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get standard");
        }
        StudentStandard standard = standardRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Standard not found"));
        return standard;
    }

    @Override
    public StudentStandard updateStandard(Long id,String role,String email,StudentStandard standard)
    {
        if(!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to update standard");
        }
        StudentStandard existingStandard = standardRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Standard not found"));
        existingStandard.setStandard(standard.getStandard());
        return standardRepository.save(existingStandard);

    }

    @Override
    public void deleteStandardById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Delete"))
        {
            throw new RuntimeException("You don't have permission to delete standard");
        }
        standardRepository.deleteById(id);
    }

    @Override
    public List<StudentStandard> getAllStandard(String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get standard");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        return standardRepository.getAllStandardByBranchCode(branchCode);
    }


}
