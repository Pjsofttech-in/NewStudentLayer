package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.BonafideRequestDTO;
import Layer.NewStudentManagement.Service.BonafideRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BonafideRequestController {

    @Autowired
    private BonafideRequestService bonafideService;

    /**
     * STUDENT: Raise a new Bonafide Request
     */
    @PostMapping("/raiseBonafideRequest")
    public ResponseEntity<BonafideRequestDTO> raiseRequest(
            @RequestParam Long studentId,
            @RequestParam String reason,
            @RequestParam String role,
            @RequestParam String email) {
        
        BonafideRequestDTO created = bonafideService.raiseRequest(studentId, reason, role, email);
        return ResponseEntity.ok(created);
    }

    /**
     * STUDENT: View their own request history
     */
    @GetMapping("/getMyBonafideRequests/{studentId}")
    public ResponseEntity<List<BonafideRequestDTO>> getMyRequests(
            @PathVariable Long studentId,
            @RequestParam String role,
            @RequestParam String email) {
        
        List<BonafideRequestDTO> requests = bonafideService.getMyRequests(studentId, role, email);
        return ResponseEntity.ok(requests);
    }

    /**
     * STAFF: View all pending requests for their branch
     */
    @GetMapping("/getPendingBonafideRequests")
    public ResponseEntity<List<BonafideRequestDTO>> getPendingRequests(
            @RequestParam String role,
            @RequestParam String email) {
        
        List<BonafideRequestDTO> requests = bonafideService.getPendingRequestsForBranch(role, email);
        return ResponseEntity.ok(requests);
    }

    /**
     * STAFF: Approve or Reject a request
     */
    @PutMapping("/processBonafideRequest/{id}")
    public ResponseEntity<BonafideRequestDTO> processRequest(
            @PathVariable Long id,
            @RequestParam String status, // "APPROVED" or "REJECTED"
            @RequestParam(required = false) String remarks,
            @RequestParam String role,
            @RequestParam String email) {
        
        BonafideRequestDTO processed = bonafideService.processRequest(id, status, remarks, role, email);
        return ResponseEntity.ok(processed);
    }
}