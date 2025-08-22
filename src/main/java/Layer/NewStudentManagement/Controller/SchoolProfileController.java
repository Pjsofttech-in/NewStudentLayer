package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.Entity.StudentSchoolProfile;
import Layer.NewStudentManagement.Service.SchoolProfileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class SchoolProfileController
{

    @Autowired
    SchoolProfileService schoolProfileService;

    @PostMapping(value = "/createSchoolProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StudentSchoolProfile createProfile(
            @RequestPart("profile") String profileJson,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestParam String role,
            @RequestParam String email) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        StudentSchoolProfile profile = mapper.readValue(profileJson, StudentSchoolProfile.class);

        return schoolProfileService.createSchoolProfile(profile, logo, role, email);
    }


    @PutMapping(value = "/updateSchoolProfile/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StudentSchoolProfile updateProfile(
            @PathVariable Long id,
            @RequestPart("profile") String profileJson,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestParam String role,
            @RequestParam String email) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();
        StudentSchoolProfile profile = mapper.readValue(profileJson, StudentSchoolProfile.class);
        return schoolProfileService.updateSchoolProfile(id, profile, logo, role, email);
    }

    @DeleteMapping("/deleteSchoolProfile/{id}")
    public String deleteProfile(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        schoolProfileService.deleteSchoolProfile(id, role, email);
        return "School Profile deleted successfully";
    }

    @GetMapping("/getSchoolProfileByBranchCode")
    public StudentSchoolProfile getProfile(
            @RequestParam String role,
            @RequestParam String email) {
        return schoolProfileService.getSchoolProfileByBranchCode(role, email);
    }
}
