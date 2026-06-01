package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.Entity.StudentFeeComponentsMaster;
import Layer.NewStudentManagement.Service.FeeComponentMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
public class FeeComponentMasterController {

    @Autowired
    FeeComponentMasterService feeComponentMasterService;

    @PostMapping(value = "/createMasterFeeComponent")
    public StudentFeeComponentsMaster createAssignment(@RequestParam String role,
                                                       @RequestParam String email,
                                                       @RequestBody StudentFeeComponentsMaster feeComponentsMaster) throws Exception {

        return feeComponentMasterService.createFeeComponentMaster(role, email, feeComponentsMaster);
    }

    @PutMapping("/updateMasterFeeComponent")
    public StudentFeeComponentsMaster updateAssignment(@RequestParam String role,
                                                       @RequestParam String email,
                                                       @RequestBody StudentFeeComponentsMaster feeComponentsMaster) {
        return feeComponentMasterService.updateFeeComponentMaster(role, email, feeComponentsMaster);
    }

    @DeleteMapping("/deleteMasterFeeComponent/{id}")
    public String deleteAssignment(@PathVariable Long id,
                                   @RequestParam String role,
                                   @RequestParam String email) {
        feeComponentMasterService.deleteFeeComponentMaster(role, email, id);
        return "Fee Component deleted successfully.";
    }

    @GetMapping("/getMasterFeeComponent/{id}")
    public StudentFeeComponentsMaster getMasterFeeComponentById(@PathVariable Long id, @RequestParam String role,
                                                                @RequestParam String email,
                                                                @RequestParam(required = false) String componentName) {
        return feeComponentMasterService.getFeeComponentMaster(role, email, id, componentName);
    }

    @GetMapping("/getAllMasterFeeComponent")
    public List<StudentFeeComponentsMaster> getAllMasterFeeComponent(@RequestParam String role,
                                                                     @RequestParam String email) {
        return feeComponentMasterService.getAllFeeComponentMasters(role, email);
    }
}
