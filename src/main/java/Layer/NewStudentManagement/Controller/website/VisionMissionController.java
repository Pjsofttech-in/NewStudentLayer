package Layer.NewStudentManagement.Controller.website;

import Layer.NewStudentManagement.Entity.website.StudentWebVisionMission;
import Layer.NewStudentManagement.Service.website.VisionMissionService;
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
public class VisionMissionController {

    @Autowired
    private VisionMissionService service;

    @PostMapping(value = "/createVisionMission", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentWebVisionMission> createVisionMission(
            @RequestPart("vm") String vmJson,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String url,
            @RequestPart(value = "directorImage", required = false) MultipartFile directorImageFile
    ) throws JsonProcessingException {

        StudentWebVisionMission vm = new ObjectMapper().readValue(vmJson, StudentWebVisionMission.class);
        return ResponseEntity.ok(service.create(vm, role, email, directorImageFile, url));
    }

    @GetMapping("/getAllVisionMissions")
    public ResponseEntity<List<StudentWebVisionMission>> getAllByBranchCode(@RequestParam String role,
                                                                     @RequestParam(required = false) String email,
                                                                     @RequestParam String branchCode,
                                                                     @RequestParam String url) {
        return ResponseEntity.ok(service.getAllByBranchCode(role, email, branchCode, url));
    }

    @GetMapping("/getVisionMissionById/{id}")
    public ResponseEntity<StudentWebVisionMission> getVisionMissionById(@PathVariable Long id,
                                                                 @RequestParam String role,
                                                                 @RequestParam String email,
                                                                 @RequestParam String url) {
        return ResponseEntity.ok(service.getById(id, role, email, url));
    }

    @PutMapping(value = "/updateVisionMission/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentWebVisionMission> updateVisionMission(
            @PathVariable Long id,
            @RequestPart("vm") String vmJson,
            @RequestPart(value = "directorImage", required = false) MultipartFile directorImage,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String url) throws JsonProcessingException {

        StudentWebVisionMission vm = new ObjectMapper().readValue(vmJson, StudentWebVisionMission.class);
        return ResponseEntity.ok(service.update(id, vm, role, email, directorImage, url));
    }

    @DeleteMapping("/deleteVisionMission/{id}")
    public ResponseEntity<String> deleteVisionMission(@PathVariable Long id,
                                                      @RequestParam String role,
                                                      @RequestParam String email,
                                                      @RequestParam String url) {
        service.delete(id, role, email, url);
        return ResponseEntity.ok("VisionMission deleted successfully");
    }
}