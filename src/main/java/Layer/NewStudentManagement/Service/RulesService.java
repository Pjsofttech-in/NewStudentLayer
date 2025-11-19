package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.Entity.StudentRules;

import java.util.List;

public interface RulesService
{
    StudentRules createRule(String role, String email,StudentRules rules);
    StudentRules updateRule(Long id, String role, String email,StudentRules rules);
    List<StudentRules> getAllRulesByBranchCode(String role, String email);
    StudentRules getRuleById(Long id,String role, String email);
    void deleteRules(Long id,String role, String email);
}
