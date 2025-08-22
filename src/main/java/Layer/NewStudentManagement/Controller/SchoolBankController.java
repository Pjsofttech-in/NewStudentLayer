package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.SchoolBankDTO;
import Layer.NewStudentManagement.Entity.StudentSchoolBank;
import Layer.NewStudentManagement.Service.SchoolBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SchoolBankController
{
    @Autowired
    SchoolBankService schoolBankService;

    @PostMapping("/createBankForSchool")
    public SchoolBankDTO createSchoolBank(
            @RequestParam String role,
            @RequestParam String email,
            @RequestBody StudentSchoolBank bank) {
        return schoolBankService.createSchoolBank(role, email, bank);
    }

    @GetMapping("/getBankById/{id}")
    public SchoolBankDTO getBankById(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        return schoolBankService.getBankById(id, role, email);
    }

    @PutMapping("/updateBankName/{id}")
    public SchoolBankDTO updateBank(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email,
            @RequestBody StudentSchoolBank bank) {
        return schoolBankService.updateBank(id, role, email, bank);
    }

    @DeleteMapping("/deleteBank/{id}")
    public String deleteBankById(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        schoolBankService.deleteBankById(id, role, email);
        return "Bank deleted successfully";
    }

    @GetMapping("/getAllBankByBranchCode")
    public List<SchoolBankDTO> getAllBanks(
            @RequestParam String role,
            @RequestParam String email) {
        return schoolBankService.getAllBank(role, email);
    }

}
