package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentRules;
import Layer.NewStudentManagement.Entity.StudentSchoolBank;
import Layer.NewStudentManagement.Repository.RulesRepository;
import Layer.NewStudentManagement.Service.RulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RulesServiceImpl implements RulesService
{

    @Autowired
    RulesRepository rulesRepository;

    @Autowired
    StaffService staffService;

    public StudentRules createRule(String role, String email, StudentRules rules)
    {
        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create Rules");
        }

        String branchCode =staffService.fetchBranchCodeByRole(role, email);

        rules.setBranchCode(branchCode);
        rules.setRole(role);
        rules.setCreatedByEmail(email);
        return rulesRepository.save(rules);

    }

    public StudentRules updateRule(Long id, String role, String email,StudentRules rules)
    {
        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create Rules");
        }

        StudentRules existingrules = rulesRepository.findById(id).orElseThrow(()-> new RuntimeException("Rules not Found"));

        existingrules.setRules(rules.getRules());

        return rulesRepository.save(existingrules);

    }

    public List<StudentRules> getAllRulesByBranchCode(String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create Rules");
        }

        String branchCode =staffService.fetchBranchCodeByRole(role, email);

        return rulesRepository.findAllRulesByBranchCode(branchCode);

    }

    public StudentRules getRuleById(Long id, String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create Rules");
        }

        StudentRules rules = rulesRepository.findById(id).orElseThrow(()-> new RuntimeException("Rules not Found"));

        return rules;


    }

    public void deleteRules(Long id,String role, String email)
    {

        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create Rules");
        }

        StudentRules rules = rulesRepository.findById(id).orElseThrow(()-> new RuntimeException("Rules not Found"));

        rulesRepository.deleteById(id);


    }
}
