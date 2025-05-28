package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentMedium;
import Layer.NewStudentManagement.Repository.MediumRepository;
import Layer.NewStudentManagement.Service.MediumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediumServiceImpl implements MediumService
{

    @Autowired
    private StaffService staffService;

    @Autowired
    private MediumRepository mediumRepository;

    @Override
    public StudentMedium createMedium(String role, String email, StudentMedium medium)
    {
        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create medium");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        medium.setBranchCode(branchCode);
        medium.setRole(role);
        medium.setCreatedByEmail(email);
        return mediumRepository.save(medium);
    }

    @Override
    public StudentMedium getMediumById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get medium");
        }
        StudentMedium medium = mediumRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Medium not found"));
        return medium;
    }

    @Override
    public StudentMedium updateMedium(Long id,String role,String email,StudentMedium medium)
    {
        if(!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to update medium");
        }
        StudentMedium existingMedium = mediumRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Medium not found"));
        existingMedium.setMedium(medium.getMedium());
        return mediumRepository.save(existingMedium);
    }

    @Override
    public void deleteMediumById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Delete"))
        {
            throw new RuntimeException("You don't have permission to delete medium");
        }
        mediumRepository.deleteById(id);
    }

    @Override
    public List<StudentMedium> getAllMedium(String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get medium");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role,email);
        return mediumRepository.findAllByBranchCode(branchCode);
    }
}
