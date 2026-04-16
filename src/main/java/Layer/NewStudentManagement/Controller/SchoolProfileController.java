package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.SchoolProfileDTO;
import Layer.NewStudentManagement.Entity.StudentSchoolProfile;
import Layer.NewStudentManagement.Service.SchoolProfileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
public class SchoolProfileController
{

    @Autowired
    SchoolProfileService schoolProfileService;

    @PostMapping(value = "/createSchoolProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SchoolProfileDTO createProfile(
            @RequestPart("profile") String profileJson,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestParam(required = false) List<MultipartFile> images,
            @RequestParam String role,
            @RequestParam String email) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        StudentSchoolProfile profile = mapper.readValue(profileJson, StudentSchoolProfile.class);

        return schoolProfileService.createSchoolProfile(profile,logo,images, role, email);
    }

    @PutMapping(value = "/updateSchoolProfile/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SchoolProfileDTO> updateProfile(
            @PathVariable Long id,
            @RequestPart("profile") String profileJson,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestPart(value = "images", required = false) List<MultipartFile> images, // ✅ ADD THIS
            @RequestParam String role,
            @RequestParam String email) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        StudentSchoolProfile profile = mapper.readValue(profileJson, StudentSchoolProfile.class);

        SchoolProfileDTO response = schoolProfileService
                .updateSchoolProfile(id, profile, logo, images, role, email);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteSchoolProfile/{id}")
    public ResponseEntity<String> deleteProfile(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {

        schoolProfileService.deleteSchoolProfile(id, role, email);
        return ResponseEntity.ok("School Profile deleted successfully");
    }

    @GetMapping("/getSchoolProfileByBranchCode")
    public ResponseEntity<SchoolProfileDTO> getProfile(
            @RequestParam String role,
            @RequestParam String email) {

        return ResponseEntity.ok(
                schoolProfileService.getSchoolProfileByBranchCode(role, email)
        );
    }

    @GetMapping("/school/{slug}")
    public ResponseEntity<SchoolProfileDTO> getSchool(@PathVariable String slug) {

        return ResponseEntity.ok(
                schoolProfileService.getBySlug(slug)
        );
    }

    @DeleteMapping("/deleteImage/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable Long imageId) {

        schoolProfileService.deleteImage(imageId);
        return ResponseEntity.ok("Image deleted successfully");
    }

}
