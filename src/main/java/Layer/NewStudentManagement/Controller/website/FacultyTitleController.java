package Layer.NewStudentManagement.Controller.website;

import Layer.NewStudentManagement.Entity.website.StudentWebFacultyTitle;
import Layer.NewStudentManagement.Service.website.FacultyTitleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://pjsofttech.in")
public class FacultyTitleController {

    @Autowired
    private FacultyTitleService service;

    @PostMapping("/createFacilityTitle")
    public ResponseEntity<StudentWebFacultyTitle> createFacilityTitle(@RequestBody StudentWebFacultyTitle webFacultyTitle,
                                                                      @RequestParam String role,
                                                                      @RequestParam String email,
                                                                      @RequestParam String url) {
        return ResponseEntity.ok(service.createFacilityTitle(webFacultyTitle, role, email, url));
    }

    @GetMapping("/getAllFacilityTitles")
    public ResponseEntity<List<StudentWebFacultyTitle>> getAllFacilityTitlesByBranchCode(@RequestParam String role,
                                                                                  @RequestParam(required = false) String email,
                                                                                  @RequestParam String url,
                                                                                  @RequestParam String branchCode) {
        return ResponseEntity.ok(service.getAllFacilityTitlesByBranchCode(role, email, url, branchCode));
    }

    @GetMapping("/getFacilityTitleById/{id}")
    public ResponseEntity<StudentWebFacultyTitle> getFacilityTitleById(@PathVariable Long id,
                                                                @RequestParam String role,
                                                                @RequestParam String email,
                                                                @RequestParam String url) {
        return ResponseEntity.ok(service.getFacilityTitleById(id, role, email, url));
    }

    @PutMapping("/updateFacilityTitle/{id}")
    public ResponseEntity<StudentWebFacultyTitle> updateFacilityTitle(@PathVariable Long id,
                                                               @RequestBody StudentWebFacultyTitle webFacultyTitle,
                                                               @RequestParam String role,
                                                               @RequestParam String email,
                                                               @RequestParam String url) {
        return ResponseEntity.ok(service.updateFacilityTitle(id, webFacultyTitle, role, email, url));
    }

    @DeleteMapping("/deleteFacilityTitle/{id}")
    public ResponseEntity<String> deleteFacilityTitle(@PathVariable Long id,
                                                      @RequestParam String role,
                                                      @RequestParam String email,
                                                      @RequestParam String url) {
        service.deleteFacilityTitle(id, role, email, url);
        return ResponseEntity.ok("Facility title deleted successfully");
    }
}
