package Layer.NewStudentManagement.Controller.website;

import Layer.NewStudentManagement.Entity.website.StudentWebCounter;
import Layer.NewStudentManagement.Service.website.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://pjsofttech.in")
public class CounterController {

    @Autowired
    private CounterService service;

    @PostMapping("/createCounter")
    public ResponseEntity<StudentWebCounter> createCounter(@RequestBody StudentWebCounter webCounter,
                                                           @RequestParam String role,
                                                           @RequestParam String email,
                                                           @RequestParam String url) {
        return ResponseEntity.ok(service.createCounter(webCounter, role, email, url));
    }

    @GetMapping("/getAllCounters")
    public ResponseEntity<List<StudentWebCounter>> getAllCountersByBranchCode(
            @RequestParam String role,
            @RequestParam(required = false) String email,
            @RequestParam String url,
            @RequestParam String branchCode) {
        return ResponseEntity.ok(service.getAllByBranchCode(role, email, url, branchCode));
    }

    @GetMapping("/getCounterById/{id}")
    public ResponseEntity<StudentWebCounter> getCounterById(@PathVariable Long id,
                                                     @RequestParam String role,
                                                     @RequestParam String email,
                                                     @RequestParam String url) {
        return ResponseEntity.ok(service.getCounterById(id, role, email, url));
    }

    @PutMapping("/updateCounter/{id}")
    public ResponseEntity<StudentWebCounter> updateCounter(@PathVariable Long id,
                                                    @RequestBody StudentWebCounter webCounter,
                                                    @RequestParam String role,
                                                    @RequestParam String email,
                                                    @RequestParam String url) {
        return ResponseEntity.ok(service.updateCounter(id, webCounter, role, email, url));
    }

    @DeleteMapping("/deleteCounter/{id}")
    public ResponseEntity<String> deleteCounter(@PathVariable Long id,
                                                @RequestParam String role,
                                                @RequestParam String email,
                                                @RequestParam String url) {
        service.deleteCounter(id, role, email, url);
        return ResponseEntity.ok("Counter deleted successfully");
    }
}