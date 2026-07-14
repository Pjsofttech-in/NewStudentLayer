package Layer.NewStudentManagement.Controller.website;

import Layer.NewStudentManagement.Entity.website.StudentWebFaculty;
import Layer.NewStudentManagement.Service.website.FacultyService;
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
public class FacultyController {

    @Autowired
    private FacultyService service;

    @PostMapping(value = "/createFacility", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentWebFaculty> createFacility(
            @RequestPart("facility") String facilityJson,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String url,
            @RequestPart("facilityImageName") MultipartFile imageFile) throws JsonProcessingException {


        StudentWebFaculty webFaculty = new ObjectMapper().readValue(facilityJson, StudentWebFaculty.class);

        return ResponseEntity.ok(service.createFacility(webFaculty, role, email, imageFile, url));
    }

    @GetMapping("/getAllFacilities")
    public ResponseEntity<List<StudentWebFaculty>> getAllFacilitiesByBranchCode(
            @RequestParam String role,
            @RequestParam(required = false) String email,
            @RequestParam String url,
            @RequestParam String branchCode) {
        return ResponseEntity.ok(service.getAllFacilitiesByBranchCode(role, email, url, branchCode));
    }

    @GetMapping("/getFacilityById/{id}")
    public ResponseEntity<StudentWebFaculty> getFacilityById(@PathVariable Long id,
                                                      @RequestParam String role,
                                                      @RequestParam String email,
                                                      @RequestParam String url) {
        return ResponseEntity.ok(service.getFacilityById(id, role, email, url));
    }

    @PutMapping(value = "/updateFacility/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentWebFaculty> updateFacility(@PathVariable Long id,
                                                     @RequestPart("facility") String facilityJson,
                                                     @RequestPart(value = "facilityImage", required = false) MultipartFile image,
                                                     @RequestParam String role,
                                                     @RequestParam String email,
                                                     @RequestParam String url) throws JsonProcessingException {

        StudentWebFaculty webFaculty = new ObjectMapper().readValue(facilityJson, StudentWebFaculty.class);
        return ResponseEntity.ok(service.updateFacility(id, webFaculty, role, email, image, url));
    }

    @DeleteMapping("/deleteFacility/{id}")
    public ResponseEntity<String> deleteFacility(@PathVariable Long id,
                                                 @RequestParam String role,
                                                 @RequestParam String email,
                                                 @RequestParam String url) {
        service.deleteFacility(id, role, email, url);
        return ResponseEntity.ok("Facility deleted successfully");
    }

}