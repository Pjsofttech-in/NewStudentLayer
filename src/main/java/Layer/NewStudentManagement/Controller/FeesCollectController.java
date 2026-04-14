package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.FeesByPaymentModeDTO;
import Layer.NewStudentManagement.DTO.FeesCollectDTO;
import Layer.NewStudentManagement.DTO.FeesFilterDTO;
import Layer.NewStudentManagement.DTO.StudentFeesHistoryDTO;
import Layer.NewStudentManagement.Entity.StudentFeesCollect;
import Layer.NewStudentManagement.Service.FeesCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
public class FeesCollectController
{

    @Autowired
    FeesCollectService feesCollectService;

    @PostMapping("/collectFeesFromStudent")
    public ResponseEntity<FeesCollectDTO> saveCollection(@RequestBody StudentFeesCollect collect, @RequestParam String role,
                                                         @RequestParam String email)
    {
        FeesCollectDTO saveFees = feesCollectService.saveFeeCollection(collect,role,email);
        return ResponseEntity.ok(saveFees);
    }

    @PutMapping("/updateCollectFeeStatus/{id}")
    public ResponseEntity<FeesCollectDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String status) {

        FeesCollectDTO updated = feesCollectService.updateFeeCollectionStatus(id, role, email, status);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/getAllCollectedFeesByFID")
    public ResponseEntity<Iterable<FeesCollectDTO>> getAllCollectedFeesBySFID(@RequestParam Long fid,@RequestParam String role,
                                                                              @RequestParam String email)
    {
        List<FeesCollectDTO>  feesCollectDTOS = feesCollectService.getAllCollectDataByStudentFeesID(fid,role,email);
        return ResponseEntity.ok(feesCollectDTOS);
    }

    @GetMapping("/getCollectedFeesByStudentId")
    public ResponseEntity<List<FeesCollectDTO>> getCollectedFeesByStudentId(@RequestParam String role, @RequestParam String email,@RequestParam Long studentId) {
        List<FeesCollectDTO> collectedFees = feesCollectService.getCollectedFeesByStudentId(role, email, studentId);
        return ResponseEntity.ok(collectedFees);
    }

    @GetMapping("/getCollectedFeesById")
    public ResponseEntity<FeesCollectDTO> getCollectedFeesById(@RequestParam String role, @RequestParam String email, @RequestParam Long id)
    {
        FeesCollectDTO feesCollect =feesCollectService.getCollectedFeesById(role, email, id);
        return ResponseEntity.ok(feesCollect);

    }

    @PostMapping("/getAllCollectedFeesByBranchForFeesHistory")
    public ResponseEntity<Page<StudentFeesHistoryDTO>> getAllCollectedFeesByBranch(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "all") String timeFrame,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestBody(required = false) FeesFilterDTO filterDTO)
    {
        Page<StudentFeesHistoryDTO> response = feesCollectService.getAllCollectedFeesByBranch(
                role, email, filterDTO, timeFrame, startDate, endDate, page, size);
        return ResponseEntity.ok(response);
    }




}
