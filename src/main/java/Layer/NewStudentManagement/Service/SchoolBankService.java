package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.SchoolBankDTO;
import Layer.NewStudentManagement.DTO.StudentDivisionDTO;
import Layer.NewStudentManagement.Entity.StudentDivision;
import Layer.NewStudentManagement.Entity.StudentSchoolBank;

import java.util.List;

public interface SchoolBankService
{
    SchoolBankDTO createSchoolBank(String role, String email, StudentSchoolBank bank);
    SchoolBankDTO getBankById(Long id, String role, String email);
    SchoolBankDTO updateBank(Long id,String role,String email,StudentSchoolBank bank);
    void deleteBankById(Long id,String role,String email);
    List<SchoolBankDTO> getAllBank(String role, String email);
}
