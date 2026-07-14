package Layer.NewStudentManagement.Controller.website;

import Layer.NewStudentManagement.Entity.website.StudentWebAboutUs;
import Layer.NewStudentManagement.Service.website.AboutUsService;
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
public class AboutUsController {

    @Autowired
    private AboutUsService service;

    @PostMapping(value = "/createAboutUs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentWebAboutUs> createAboutUs(
            @RequestPart("aboutUs") String aboutUsJson,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String url,
            @RequestParam("aboutUsImageName") MultipartFile aboutUsImage) throws JsonProcessingException {


        StudentWebAboutUs webAboutUs = new ObjectMapper().readValue(aboutUsJson, StudentWebAboutUs.class);

        return ResponseEntity.ok(service.createAboutUs(webAboutUs, role, email, aboutUsImage, url));
    }

    @GetMapping("/getAllAboutUs")
    public ResponseEntity<List<StudentWebAboutUs>> getAllAboutUsByBranchCode(
            @RequestParam String role,
            @RequestParam(required = false) String email,
            @RequestParam String url,
            @RequestParam String branchCode) {

        return ResponseEntity.ok(service.getAllAboutUsByBranchCode(role, email, url, branchCode));
    }

    @GetMapping("/getAboutUsById/{id}")
    public ResponseEntity<StudentWebAboutUs> getAboutUsById(@PathVariable int id,
                                                     @RequestParam String role,
                                                     @RequestParam String email,
                                                     @RequestParam String url) {
        return ResponseEntity.ok(service.getAboutUsById(id, role, email, url));
    }

    @PutMapping(value = "/updateAboutUs/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentWebAboutUs> updateAboutUs(@PathVariable int id,
                                                    @RequestPart("aboutUs") String aboutUsJson,
                                                    @RequestParam String role,
                                                    @RequestParam String email,
                                                    @RequestParam String url,
                                                    @RequestPart(value = "aboutUsImage", required = false) MultipartFile aboutUsImage) throws JsonProcessingException {

        StudentWebAboutUs webAboutUs = new ObjectMapper().readValue(aboutUsJson, StudentWebAboutUs.class);
        return ResponseEntity.ok(service.updateAboutUs(id, webAboutUs, role, email, aboutUsImage, url));
    }

    @DeleteMapping("/deleteAboutUs/{id}")
    public ResponseEntity<String> deleteAboutUs(@PathVariable int id,
                                                @RequestParam String role,
                                                @RequestParam String email,
                                                @RequestParam String url) {
        service.deleteAboutUs(id, role, email, url);
        return ResponseEntity.ok("AboutUs deleted successfully");
    }

}