package Layer.NewStudentManagement.Controller.website;

import Layer.NewStudentManagement.Entity.website.StudentWebAwardsAndAccolades;
import Layer.NewStudentManagement.Service.website.AwardsAndAccoladesService;
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
public class AwardsAndAccoladesController {

    @Autowired
    private AwardsAndAccoladesService service;

    @PostMapping(value = "/createAward", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentWebAwardsAndAccolades> createAward(
            @RequestPart("award") String awardJson,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String url,
            @RequestParam("awardImageName") MultipartFile awardImageFile) throws JsonProcessingException {


        StudentWebAwardsAndAccolades award = new ObjectMapper().readValue(awardJson, StudentWebAwardsAndAccolades.class);

        return ResponseEntity.ok(service.createAward(award, role, email, awardImageFile, url));
    }

    @GetMapping("/getAllAwards")
    public ResponseEntity<List<StudentWebAwardsAndAccolades>> getAllAwardsByBranchCode(
            @RequestParam String role,
            @RequestParam(required = false) String email,
            @RequestParam String url,
            @RequestParam String branchCode) {

        return ResponseEntity.ok(service.getAllAwardsByBranchCode(role, email, url, branchCode));
    }

    @GetMapping("/getAwardById/{id}")
    public ResponseEntity<StudentWebAwardsAndAccolades> getAwardById(@PathVariable Long id,
                                                              @RequestParam String role,
                                                              @RequestParam String email,
                                                              @RequestParam String url) {
        return ResponseEntity.ok(service.getAwardById(id, role, email, url));
    }

    @PutMapping(value = "/updateAward/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentWebAwardsAndAccolades> updateAward(@PathVariable Long id,
                                                             @RequestPart("award") String awardJson,
                                                             @RequestPart(value = "awardImage", required = false) MultipartFile awardImage,
                                                             @RequestParam String role,
                                                             @RequestParam String email,
                                                             @RequestParam String url) throws JsonProcessingException {

        StudentWebAwardsAndAccolades award = new ObjectMapper().readValue(awardJson, StudentWebAwardsAndAccolades.class);
        return ResponseEntity.ok(service.updateAward(id, award, role, email, awardImage, url));
    }

    @DeleteMapping("/deleteAward/{id}")
    public ResponseEntity<String> deleteAward(@PathVariable Long id,
                                              @RequestParam String role,
                                              @RequestParam String email,
                                              @RequestParam String url) {
        service.deleteAward(id, role, email, url);
        return ResponseEntity.ok("Award deleted successfully");
    }

}