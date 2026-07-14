package Layer.NewStudentManagement.Controller.website;

import Layer.NewStudentManagement.Entity.website.StudentWebManuBar;
import Layer.NewStudentManagement.Service.website.ManuBarService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://pjsofttech.in")
public class ManuBarController {

    @Autowired
    private ManuBarService service;

    @PostMapping(value = "/createManuBar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentWebManuBar> createManuBar(
            @RequestPart("manuBar") String manuBarJson,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String url,
            @RequestParam("menubarImageName") MultipartFile imageFile) throws JsonProcessingException {


        StudentWebManuBar webManuBar = new ObjectMapper().readValue(manuBarJson, StudentWebManuBar.class);

        return ResponseEntity.ok(service.createManuBar(webManuBar, role, email, imageFile, url));
    }

    @GetMapping("/getAllManuBars")
    public ResponseEntity<List<StudentWebManuBar>> getAllByBranchCode(@RequestParam String role,
                                                               @RequestParam(required = false) String email,
                                                               @RequestParam String url,
                                                               @RequestParam String branchCode) {
        return ResponseEntity.ok(service.getAllByBranchCode(role, email, url, branchCode));
    }

    @GetMapping("/getManuBarById/{id}")
    public ResponseEntity<StudentWebManuBar> getManuBarById(@PathVariable Long id,
                                                     @RequestParam String role,
                                                     @RequestParam String email,
                                                     @RequestParam String url) {
        return ResponseEntity.ok(service.getManuBarById(id, role, email, url));
    }

    @PutMapping(value = "/updateManuBar/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentWebManuBar> updateManuBar(@PathVariable Long id,
                                                    @RequestPart("manuBar") String manuBarJson,
                                                    @RequestPart(value = "menubarImage", required = false) MultipartFile menubarImage,
                                                    @RequestParam String role,
                                                    @RequestParam String email,
                                                    @RequestParam String url) throws JsonProcessingException {

        StudentWebManuBar webManuBar = new ObjectMapper().readValue(manuBarJson, StudentWebManuBar.class);
        return ResponseEntity.ok(service.updateManuBar(id, webManuBar, role, email, menubarImage, url));
    }

    @DeleteMapping("/deleteManuBar/{id}")
    public ResponseEntity<String> deleteManuBar(@PathVariable Long id,
                                                @RequestParam String role,
                                                @RequestParam String email,
                                                @RequestParam String url) {
        service.deleteManuBar(id, role, email, url);
        return ResponseEntity.ok("ManuBar deleted successfully");
    }
}
