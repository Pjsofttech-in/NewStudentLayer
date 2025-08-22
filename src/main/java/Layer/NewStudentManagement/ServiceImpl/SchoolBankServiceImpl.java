package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.SchoolBankDTO;
import Layer.NewStudentManagement.Entity.StudentSchoolBank;
import Layer.NewStudentManagement.Repository.SchoolBankRepository;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.SchoolBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchoolBankServiceImpl implements SchoolBankService
{
    @Autowired
    private StaffService staffService;

    @Autowired
    private SchoolBankRepository schoolBankRepository;

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public SchoolBankDTO createSchoolBank(String role, String email, StudentSchoolBank bank)
    {
        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create Bank");
        }

        String branchCode =staffService.fetchBranchCodeByRole(role, email);

        bank.setBranchCode(branchCode);
        bank.setRole(role);
        bank.setCreatedByEmail(email);
        StudentSchoolBank save = schoolBankRepository.save(bank);
        return mapToBankDTO(save);
    }

    @Override
    public SchoolBankDTO getBankById(Long id, String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to Get Bank");
        }
        StudentSchoolBank bank = schoolBankRepository.findById(id).orElseThrow(()->new RuntimeException("Bank not found"));
        return mapToBankDTO(bank);
    }

    @Override
    public SchoolBankDTO updateBank(Long id,String role,String email,StudentSchoolBank bank)
    {
        if(!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to Update Bank");
        }

        StudentSchoolBank existingBank = schoolBankRepository.findById(id).orElseThrow(()->new RuntimeException("Bank not found"));
        existingBank.setBankName(bank.getBankName());
        StudentSchoolBank saved = schoolBankRepository.save(existingBank);
        return mapToBankDTO(saved);

    }

    @Override
    public void deleteBankById(Long id,String role,String email)
    {

        if(!staffService.hasPermission(role,email,"Delete"))
        {
            throw new RuntimeException("You don't have permission to Delete Bank");
        }
        schoolBankRepository.deleteById(id);

    }

    @Override
    public List<SchoolBankDTO> getAllBank(String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to Get All Bank");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        List<StudentSchoolBank> banks = schoolBankRepository.findAllByBranchCode(branchCode);
        return banks.stream().map(this::mapToBankDTO)
            .collect(Collectors.toList());

    }

    private SchoolBankDTO mapToBankDTO(StudentSchoolBank bank) {
        SchoolBankDTO dto = new SchoolBankDTO();
        dto.setId(bank.getId());
        dto.setBankName(bank.getBankName());
        if (bank.getSchoolProfile() != null && bank.getSchoolProfile().getId() != null) {
            dto.setSchoolProfileId(bank.getSchoolProfile().getId());
        } else {
            dto.setSchoolProfileId(0L); // or null if preferred
        }
        dto.setCreatedByEmail(bank.getCreatedByEmail());
        dto.setRole(bank.getRole());
        dto.setBranchCode(bank.getBranchCode());

        return dto;
    }
}
