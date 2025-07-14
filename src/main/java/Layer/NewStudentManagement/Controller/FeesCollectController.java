package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.FeesCollectDTO;
import Layer.NewStudentManagement.Entity.StudentFeesCollect;
import Layer.NewStudentManagement.Service.FeesCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
public class FeesCollectController
{

    @Autowired
    FeesCollectService feesCollectService;

    @PostMapping("/collegeFeesFromStudent")
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

    @GetMapping("/getAllCollectedFeesBySFID")
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
}
