package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.*;
import Layer.NewStudentManagement.Entity.StudentFees;
import Layer.NewStudentManagement.Service.FeesService;
import Layer.NewStudentManagement.ServiceImpl.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class FeesController
{
    @Autowired
    StaffService staffService;

    @Autowired
    private FeesService feesService;

    @PostMapping("/assignFeesToStudent")
    public ResponseEntity<StudentFeesDTO> createStudentFees(@RequestParam String role, @RequestParam String email, @RequestBody StudentFees fees)
    {
        StudentFeesDTO fees1 = feesService.assignFeesToStudent(role, email, fees);
        return ResponseEntity.ok(fees1);
    }

    @PutMapping("/updateStudentFees/{id}")
    public ResponseEntity<StudentFeesDTO> updateStudentFees(@PathVariable Long id, @RequestParam String role, @RequestParam String email,@RequestBody StudentFees fees)
    {
        StudentFeesDTO feesDTO = feesService.updateFees(id, fees, role,email);
        return ResponseEntity.ok(feesDTO);
    }

    @GetMapping("/getStudentFeesById/{id}")
    public ResponseEntity<StudentFeesDTO> getStudentFeesById(@PathVariable Long id,@RequestParam String role, @RequestParam String email)
    {
        StudentFeesDTO feesDTO =feesService.getFeesById(id, role, email);
        return ResponseEntity.ok(feesDTO);
    }


    @DeleteMapping("/deleteStudentFees/{id}")
    public ResponseEntity<Void> deleteStudentFees(@RequestParam Long id,@RequestParam String role, @RequestParam String email)
    {
        feesService.deleteFees(id,role,email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getStudentFeesByStudentId")
    public ResponseEntity<List<StudentFeesDTO>> getAllFeesByStudentId(@RequestParam Long studentId,@RequestParam String role, @RequestParam String email) {

        List<StudentFeesDTO> feesDTO =feesService.getAllFeesForStudent(studentId,role, email);
        return ResponseEntity.ok(feesDTO);
    }

    @PostMapping("/getAllFeesForStudentWithFilter")
    public ResponseEntity<Page<StudentFeesDTO>> filterStudentFees(
            @RequestBody FeesFilterDTO filterDTO,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        Page<StudentFeesDTO> feesPage = feesService.getAllFeesWithFilter(filterDTO, branchCode, page, size);

        return ResponseEntity.ok(feesPage);
    }

    @PostMapping("/feesRevenewByBranch")
    public ResponseEntity<FeesRevenueProjection> getFeesRevenueByBranch(@RequestParam String role, @RequestParam String email,
                                                                        @RequestParam(required = false) String timeFrame,
                                                                        @RequestParam(required = false) LocalDate startDate,
                                                                        @RequestParam(required = false) LocalDate endDate,
                                                                        @RequestBody FeesRevenueFilterDTO filters) {

        FeesRevenueProjection summary = feesService.getFeesRevenueByBranch(role,email,timeFrame,startDate,endDate,filters);
        return ResponseEntity.ok(summary);
    }


}
