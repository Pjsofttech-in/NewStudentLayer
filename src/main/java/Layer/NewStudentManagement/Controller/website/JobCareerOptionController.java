package Layer.NewStudentManagement.Controller.website;

import Layer.NewStudentManagement.DTO.WebJobCareerOptionDTO;
import Layer.NewStudentManagement.Entity.website.StudentWebJobCareerOption;
import Layer.NewStudentManagement.Service.website.JobCareerOptionService;
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
public class JobCareerOptionController {

    @Autowired
    private JobCareerOptionService service;

    @PostMapping(value = "/createJobCareerOption", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WebJobCareerOptionDTO> create(
            @RequestPart("job") String jobJson,
            @RequestPart("resumeFile") MultipartFile resumeFile,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String url,
            @RequestParam Long webHRDetailsId) throws JsonProcessingException {

        StudentWebJobCareerOption job = new ObjectMapper().readValue(jobJson, StudentWebJobCareerOption.class);
        return ResponseEntity.ok(service.create(job, role, email, resumeFile, url, webHRDetailsId));
    }

    @GetMapping("/getAllJobCareerOptions")
    public ResponseEntity<List<WebJobCareerOptionDTO>> getAllByBranchCode(
            @RequestParam String role,
            @RequestParam(required = false) String email,
            @RequestParam String url,
            @RequestParam String branchCode) {
        return ResponseEntity.ok(service.getAllByBranchCode(role, email, url, branchCode));
    }

    @GetMapping("/getJobCareerOptionById/{id}")
    public ResponseEntity<WebJobCareerOptionDTO> getById(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String url) {
        return ResponseEntity.ok(service.getById(id, role, email, url));
    }

    @PutMapping(value = "/updateJobCareerOption/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WebJobCareerOptionDTO> update(
            @PathVariable Long id,
            @RequestPart("job") String jobJson,
            @RequestPart(value = "resumeFile", required = false) MultipartFile resumeFile,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String url,
            @RequestParam(required = false) Long webHRDetailsId) throws Exception {

        StudentWebJobCareerOption job = new ObjectMapper().readValue(jobJson, StudentWebJobCareerOption.class);
        return ResponseEntity.ok(service.update(id, job, role, email, resumeFile, url, webHRDetailsId));
    }

    @DeleteMapping("/deleteJobCareerOption/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String url) {
        service.delete(id, role, email, url);
        return ResponseEntity.ok("Job post deleted successfully");
    }
}