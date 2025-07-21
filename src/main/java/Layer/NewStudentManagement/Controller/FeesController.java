package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.FeesRevenueProjection;
import Layer.NewStudentManagement.DTO.StudentFeesDTO;
import Layer.NewStudentManagement.DTO.StudentFeesFilterRequest;
import Layer.NewStudentManagement.Entity.StudentFees;
import Layer.NewStudentManagement.Service.FeesService;
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

//    @GetMapping("/getAllStudentFees")
//    public ResponseEntity<List<StudentFeesDTO>> getAllStudentFees(@RequestParam String role, @RequestParam String email)
//    {
//        List<StudentFeesDTO> feesDTO =feesService.getAllFees(role, email);
//        return ResponseEntity.ok(feesDTO);
//    }

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
    public Page<StudentFeesDTO> filterStudentFees(
            @RequestBody StudentFeesFilterRequest request,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return feesService.filterStudentFees(request, role, email, page, size);
    }

    @GetMapping("/feesRevenewByBranch")
    public ResponseEntity<FeesRevenueProjection> getFeesRevenueByBranch(@RequestParam String role, @RequestParam String email,
                                                                        @RequestParam String timeFrame,
                                                                        @RequestParam(required = false) LocalDate startDate,
                                                                        @RequestParam(required = false) LocalDate endDate) {

        FeesRevenueProjection summary = feesService.getFeesRevenueByBranch(role,email,timeFrame,startDate,endDate);
        return ResponseEntity.ok(summary);
    }


}
