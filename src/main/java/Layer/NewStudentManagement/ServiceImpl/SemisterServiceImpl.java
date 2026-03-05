package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentSemister;
import Layer.NewStudentManagement.Enum.Role;
import Layer.NewStudentManagement.Repository.SemisterRepository;
import Layer.NewStudentManagement.Service.SemisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SemisterServiceImpl implements SemisterService
{

    @Autowired
    StaffService staffService;

    @Autowired
    SemisterRepository semisterRepository;

    @Override
    public StudentSemister createSemister(String role, String email, StudentSemister semister)
    {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to create Semister");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        semister.setRole(Role.valueOf(role));
        semister.setCreatedByEmail(email);
        semister.setBranchCode(branchCode);

        return semisterRepository.save(semister);

    }

    public StudentSemister updateSemister(Long id,String role, String email,StudentSemister semister)
    {
        if (!staffService.hasPermission(role, email, "PUT")) {
            throw new RuntimeException("You don't have permission to Update Semister");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        StudentSemister updateSemister = semisterRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Semister Not Found"));

        updateSemister.setBranchCode(branchCode);

        if(semister.getSemister()!=null && !semister.getSemister().isEmpty())
        {
            updateSemister.setSemister(semister.getSemister());
        }
        return semisterRepository.save(updateSemister);

    }

    @Override
    public List<StudentSemister> getAllSemister(String role, String email)
    {
        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to View Semister");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        return semisterRepository.findAllSemisterByBranchCode(branchCode);

    }

    @Override
    public StudentSemister getSemister(Long id,String role, String email)
    {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Update Semister");
        }

        StudentSemister semister = semisterRepository.findById(id).orElseThrow(()-> new RuntimeException("Semister Not Found"));
        return semister;

    }

    @Override
    public void deleteSemister(Long id,String role, String email)
    {
        if (!staffService.hasPermission(role, email, "DELETE")) {
            throw new RuntimeException("You don't have permission to delete Semister");
        }

        StudentSemister semister = semisterRepository.findById(id).orElseThrow(()-> new RuntimeException("Semister Not Found"));

        semisterRepository.deleteById(id);
    }
}
