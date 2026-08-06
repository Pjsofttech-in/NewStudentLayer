package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.MiscFeePaymentRequestDTO;
import Layer.NewStudentManagement.DTO.StudentMiscFeeDTO;
import Layer.NewStudentManagement.Service.StudentMiscFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StudentMiscFeeController {

    @Autowired
    private StudentMiscFeeService miscFeeService;

    /**
     * Assigns a new Miscellaneous Fee to a student's academic year record.
     */
    @PostMapping("/assignMiscFee")
    public ResponseEntity<StudentMiscFeeDTO> assignMiscFee(
            @RequestParam String role,
            @RequestParam String email,
            @RequestBody StudentMiscFeeDTO requestDTO) {
        
        StudentMiscFeeDTO createdFee = miscFeeService.assignMiscFee(role, email, requestDTO);
        return ResponseEntity.ok(createdFee);
    }

    /**
     * Updates an existing Miscellaneous Fee (e.g., modifying amount or description).
     */
    @PutMapping("/updateMiscFee/{id}")
    public ResponseEntity<StudentMiscFeeDTO> updateMiscFee(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email,
            @RequestBody StudentMiscFeeDTO requestDTO) {
        
        StudentMiscFeeDTO updated = miscFeeService.updateMiscFee(id, role, email, requestDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Handles manual offline/front-desk payments and generates a receipt for the misc fee.
     */
    @PostMapping("/payMiscFee/{id}")
    public ResponseEntity<StudentMiscFeeDTO> payMiscFee(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email,
            @RequestBody MiscFeePaymentRequestDTO requestDTO) {
        
        StudentMiscFeeDTO paidFee = miscFeeService.payMiscFee(id, role, email, requestDTO);
        return ResponseEntity.ok(paidFee);
    }

    /**
     * Retrieves all miscellaneous fees assigned to a specific student's academic year record (StudentFees).
     */
    @GetMapping("/getMiscFeeByStudentFeeId/{studentFeesId}")
    public ResponseEntity<List<StudentMiscFeeDTO>> getMiscFeesByStudentFeesId(
            @PathVariable Long studentFeesId,
            @RequestParam String role,
            @RequestParam String email) {
        
        List<StudentMiscFeeDTO> fees = miscFeeService.getMiscFeesByStudentFeesId(studentFeesId, role, email);
        return ResponseEntity.ok(fees);
    }

    /**
     * Retrieves all miscellaneous fees by id (StudentFees).
     */
    @GetMapping("/getMiscFee/{miscFeesId}")
    public ResponseEntity<StudentMiscFeeDTO> getMiscFeesById(
            @PathVariable Long miscFeesId,
            @RequestParam String role,
            @RequestParam String email) {

        StudentMiscFeeDTO fees = miscFeeService.getMiscFeesById(miscFeesId, role, email);
        return ResponseEntity.ok(fees);
    }

    /**
     * Deletes a miscellaneous fee (only allows deletion if no payments have been made against it).
     */
    @DeleteMapping("/deleteMiscFee/{id}")
    public ResponseEntity<String> deleteMiscFee(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        
        miscFeeService.deleteMiscFee(id, role, email);
        return ResponseEntity.ok("Miscellaneous fee deleted successfully.");
    }
}