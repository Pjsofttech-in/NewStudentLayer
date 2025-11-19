package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.Entity.StudentRules;
import Layer.NewStudentManagement.Service.RulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RulesController
{

    @Autowired
    RulesService rulesService;

    @PostMapping("/createRules")
    public ResponseEntity<StudentRules> createRules(@RequestParam String role, @RequestParam String email,
                                                    @RequestBody StudentRules rules)
    {
        StudentRules rules1 = rulesService.createRule(role, email, rules);
        return ResponseEntity.ok(rules1);
    }

    @PutMapping("/updateRules/{id}")
    public ResponseEntity<StudentRules> updateRules(@PathVariable Long id,@RequestParam String role, @RequestParam String email,
                                                    @RequestBody StudentRules rules)
    {
        StudentRules rules1 = rulesService.updateRule(id,role, email, rules);
        return ResponseEntity.ok(rules1);
    }

    @GetMapping("/getRuleById/{id}")
    public ResponseEntity<StudentRules> getRulById(@PathVariable Long id,@RequestParam String role, @RequestParam String email)
    {
        StudentRules rules1 = rulesService.getRuleById(id,role, email);
        return ResponseEntity.ok(rules1);
    }


    @GetMapping("/getAllRulesByBranchCode")
    public ResponseEntity<List<StudentRules>> getAllByBranchCode(@RequestParam String role, @RequestParam String email)
    {
        List<StudentRules> rules = rulesService.getAllRulesByBranchCode(role, email);
        return ResponseEntity.ok(rules);
    }

    @DeleteMapping("/deleteRules/{id}")
    public String deleteRule(@PathVariable Long id,@RequestParam String role, @RequestParam String email)
    {
        rulesService.deleteRules(id, role, email);
        return "Rules deleted successfully with ID: " + id;
    }

}
