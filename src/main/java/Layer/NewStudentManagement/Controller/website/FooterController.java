package Layer.NewStudentManagement.Controller.website;

import Layer.NewStudentManagement.Entity.website.StudentWebFooter;
import Layer.NewStudentManagement.Service.website.FooterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://pjsofttech.in")
public class FooterController {

    @Autowired
    private FooterService service;

    @PostMapping("/createFooter")
    public ResponseEntity<StudentWebFooter> createFooter(@RequestBody StudentWebFooter webFooter,
                                                         @RequestParam String role,
                                                         @RequestParam String email,
                                                         @RequestParam String url) {
        return ResponseEntity.ok(service.createFooter(webFooter, role, email, url));
    }

    @GetMapping("/getAllFooters")
    public ResponseEntity<List<StudentWebFooter>> getAllFootersByBranchCode(@RequestParam String role,
                                                                     @RequestParam(required = false) String email,
                                                                     @RequestParam String url,
                                                                     @RequestParam String branchCode) {
        return ResponseEntity.ok(service.getAllFootersByBranchCode(role, email, url, branchCode));
    }

    @GetMapping("/getFooterById/{id}")
    public ResponseEntity<StudentWebFooter> getFooterById(@PathVariable Long id,
                                                   @RequestParam String role,
                                                   @RequestParam String email,
                                                   @RequestParam String url) {
        return ResponseEntity.ok(service.getFooterById(id, role, email, url));
    }

    @PutMapping("/updateFooter/{id}")
    public ResponseEntity<StudentWebFooter> updateFooter(@PathVariable Long id,
                                                  @RequestBody StudentWebFooter webFooter,
                                                  @RequestParam String role,
                                                  @RequestParam String email,
                                                  @RequestParam String url) {
        return ResponseEntity.ok(service.updateFooter(id, webFooter, role, email, url));
    }

    @DeleteMapping("/deleteFooter/{id}")
    public ResponseEntity<String> deleteFooter(@PathVariable Long id,
                                               @RequestParam String role,
                                               @RequestParam String email,
                                               @RequestParam String url) {
        service.deleteFooter(id, role, email, url);
        return ResponseEntity.ok("Footer deleted successfully");
    }

}