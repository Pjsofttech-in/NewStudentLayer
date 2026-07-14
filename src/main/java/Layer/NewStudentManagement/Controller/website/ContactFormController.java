package Layer.NewStudentManagement.Controller.website;

import Layer.NewStudentManagement.Entity.website.StudentWebContactForm;
import Layer.NewStudentManagement.Service.website.ContactFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://pjsofttech.in")
public class ContactFormController {

    @Autowired
    private ContactFormService service;

    @PostMapping("/createContactForm")
    public ResponseEntity<StudentWebContactForm> createContactForm(@RequestBody StudentWebContactForm webContactForm,
                                                                   @RequestParam String role,
                                                                   @RequestParam(required = false) String email,
                                                                   @RequestParam String url,
                                                                   @RequestParam(required = false) String branchCode) {
        return ResponseEntity.ok(service.create(webContactForm, role, email, url,branchCode));
    }

    @GetMapping("/getAllContactForms")
    public ResponseEntity<List<StudentWebContactForm>> getAllContactFormsByBranchCode(
            @RequestParam String role,
            @RequestParam(required = false) String email,
            @RequestParam String url,
            @RequestParam String branchCode) {
        return ResponseEntity.ok(service.getAllByBranchCode(role, email, url, branchCode));
    }

    @GetMapping("/getContactFormById/{id}")
    public ResponseEntity<StudentWebContactForm> getContactFormById(@PathVariable Long id,
                                                             @RequestParam String role,
                                                             @RequestParam String email,
                                                             @RequestParam String url) {
        return ResponseEntity.ok(service.getById(id, role, email, url));
    }

    @PutMapping("/updateContactForm/{id}")
    public ResponseEntity<StudentWebContactForm> updateContactForm(@PathVariable Long id,
                                                            @RequestBody StudentWebContactForm webContactForm,
                                                            @RequestParam String role,
                                                            @RequestParam String email,
                                                            @RequestParam String url) {
        return ResponseEntity.ok(service.update(id, webContactForm, role, email, url));
    }

    @DeleteMapping("/deleteContactForm/{id}")
    public ResponseEntity<String> deleteContactForm(@PathVariable Long id,
                                                    @RequestParam String role,
                                                    @RequestParam String email,
                                                    @RequestParam String url) {
        service.delete(id, role, email, url);
        return ResponseEntity.ok("ContactForm deleted successfully");
    }
}