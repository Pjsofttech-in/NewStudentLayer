package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StudentCertificationDTO;
import Layer.NewStudentManagement.Service.StudentCertificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StudentCertificationController {

    @Autowired
    private StudentCertificationService certificationService;

    @PostMapping("/createCertification")
    public ResponseEntity<StudentCertificationDTO> createCertification(
            @RequestParam String role, 
            @RequestParam String email, 
            @RequestBody StudentCertificationDTO certificationDTO) {
        
        StudentCertificationDTO createdCertification = certificationService.createCertification(role, email, certificationDTO);
        return ResponseEntity.ok(createdCertification);
    }

    @GetMapping("/getAllCertifications")
    public ResponseEntity<List<StudentCertificationDTO>> getAllCertifications(
            @RequestParam String role, 
            @RequestParam String email, 
            @RequestParam String branchCode) {
        
        List<StudentCertificationDTO> certifications = certificationService.getAllCertifications(role, email, branchCode);
        return ResponseEntity.ok(certifications);
    }

    @GetMapping("/getCertificationById/{id}")
    public ResponseEntity<StudentCertificationDTO> getCertificationById(
            @PathVariable Long id,
            @RequestParam String role, 
            @RequestParam String email) {
        
        StudentCertificationDTO certification = certificationService.getCertificationById(id, role, email);
        return ResponseEntity.ok(certification);
    }

    @PutMapping("/updateCertification/{id}")
    public ResponseEntity<StudentCertificationDTO> updateCertification(
            @PathVariable Long id,
            @RequestParam String role, 
            @RequestParam String email, 
            @RequestBody StudentCertificationDTO certificationDTO) {
        
        StudentCertificationDTO updatedCertification = certificationService.updateCertification(id, role, email, certificationDTO);
        return ResponseEntity.ok(updatedCertification);
    }

    @DeleteMapping("/deleteCertification/{id}")
    public ResponseEntity<String> deleteCertification(
            @PathVariable Long id,
            @RequestParam String role, 
            @RequestParam String email) {
        
        certificationService.deleteCertification(id, role, email);
        return ResponseEntity.ok("Certification deleted successfully");
    }

    @GetMapping("/getCertificationsByStreamId/{streamId}")
    public ResponseEntity<List<StudentCertificationDTO>> getCertificationsByStreamId(
            @PathVariable Long streamId,
            @RequestParam String role, 
            @RequestParam String email) {
        
        List<StudentCertificationDTO> certifications = certificationService.getCertificationsByStreamId(streamId, role, email);
        return ResponseEntity.ok(certifications);
    }
}