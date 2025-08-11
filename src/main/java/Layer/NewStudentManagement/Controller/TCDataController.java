package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.TCDTO;
import Layer.NewStudentManagement.Entity.StudentTcData;
import Layer.NewStudentManagement.Service.TCDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
public class TCDataController
{
    @Autowired
    TCDataService tcDataService;


    @PostMapping("/generateTCForStudent")
    public ResponseEntity<TCDTO> generateTc(
            @RequestParam Long studentId,
            @RequestParam String role,
            @RequestParam String email) {
        TCDTO tcData = tcDataService.generateTc(studentId, role, email);
        return ResponseEntity.ok(tcData);
    }

    @GetMapping("/getAllTCHistoryByBranchCode")
    public ResponseEntity<List<TCDTO>> getAllTCHistoryByBranchCode(@RequestParam String role,
                                                                          @RequestParam String email)
    {
        List<TCDTO> tcData = tcDataService.getAllTCByBranchCode(role, email);
        return ResponseEntity.ok(tcData);
    }

    @GetMapping("/getAllTCHistoryByStudentId")
    public ResponseEntity<List<TCDTO>> getAllTCHistoryByStudentId(@RequestParam Long studentId,
                                                                          @RequestParam String role,
                                                                          @RequestParam String email)
    {
        List<TCDTO> tcData = tcDataService.getAllTCByStudentId(studentId,role, email);
        return ResponseEntity.ok(tcData);
    }
}
