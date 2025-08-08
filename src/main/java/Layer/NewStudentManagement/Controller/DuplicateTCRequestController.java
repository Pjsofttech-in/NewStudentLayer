package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.Entity.StudentDuplicateTCRequest;
import Layer.NewStudentManagement.Service.DuplicteTCRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DuplicateTCRequestController
{

    @Autowired
    DuplicteTCRequestService tcRequestService;

    @PostMapping("/createRequestForDuplicateTC")
    public ResponseEntity<StudentDuplicateTCRequest> studentDuplicateTCRequest(@RequestParam Long studentId,
                                                                               @RequestBody StudentDuplicateTCRequest request,
                                                                               @RequestParam String role, @RequestParam String email)
    {
        StudentDuplicateTCRequest tcRequest = tcRequestService.createDuplicateTCRequest(studentId, request, role, email);
        return ResponseEntity.ok(tcRequest);

    }


    @PutMapping("/approveTCRequest/{requestId}")
    public ResponseEntity<String> approveDuplicateTCRequest(@PathVariable Long requestId,
                                                            @RequestParam String role,
                                                            @RequestParam String email) {
        tcRequestService.approveDuplicateTCRequest(requestId,role,email);
        return ResponseEntity.ok("Duplicate TC Request approved successfully and TC marked as generated for the student.");
    }

    @GetMapping("/getAllTCRequest")
    public ResponseEntity<List<StudentDuplicateTCRequest>> getAllDuplicateTCRequests( @RequestParam String role,
                                                                                       @RequestParam String email) {
        List<StudentDuplicateTCRequest> requests = tcRequestService.getAllDuplicateTCRequests(role, email);
        return ResponseEntity.ok(requests);
    }
}

