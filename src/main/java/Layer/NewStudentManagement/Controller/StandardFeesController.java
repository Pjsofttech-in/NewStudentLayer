package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.FeesFilterDTO;
import Layer.NewStudentManagement.DTO.StandardFeesRequestDTO;
import Layer.NewStudentManagement.Entity.StudentStandardFees;
import Layer.NewStudentManagement.Service.StandardFeesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class StandardFeesController
{
    @Autowired
    StandardFeesService standardFeesService;

    @PostMapping("/createStandardFees")
    public ResponseEntity<StandardFeesRequestDTO> createStandardFees(@RequestParam String role, @RequestParam String email, @RequestBody StudentStandardFees standardFees)
    {
        StandardFeesRequestDTO standardFees1 = standardFeesService.createStandardFees(role,email,standardFees);
        return ResponseEntity.ok(standardFees1);
    }

    @GetMapping("/getAllStandardFees")
    public ResponseEntity<List<StandardFeesRequestDTO>> getAllStandardFees(@RequestParam String role, @RequestParam String email,
                                                                           @RequestParam(required = false) String branchCode)
    {
        List<StandardFeesRequestDTO> standardFees = standardFeesService.getAllStandardFees(role,email,branchCode);
        return ResponseEntity.ok(standardFees);
    }


    @GetMapping("/getStandardFeesById/{sfid}")
    public ResponseEntity<StandardFeesRequestDTO> getAllStandardFeesById(@RequestParam String role, @RequestParam String email,@PathVariable Long sfid)
    {
        StandardFeesRequestDTO standardFees = standardFeesService.getStandardFeesById(role, email, sfid);
        return ResponseEntity.ok(standardFees);
    }

    @PostMapping("/getStandardFeesByFilter")
    public ResponseEntity<List<StandardFeesRequestDTO>> filterFees(@RequestParam String role, @RequestParam String email,@RequestBody FeesFilterDTO filterDTO) {
        List<StandardFeesRequestDTO> fees = standardFeesService.filterFees(
               role, email, filterDTO);
        return ResponseEntity.ok(fees);
    }



    @PutMapping("/updateStandardFees/{sfid}")
    public ResponseEntity<StandardFeesRequestDTO> updateStandardFees(@RequestParam String role, @RequestParam String email,@PathVariable Long sfid, @RequestBody StudentStandardFees updatedStandardFees)
    {
        StandardFeesRequestDTO standardFees1 =standardFeesService.updateStandardFees(role,email,sfid,updatedStandardFees);
        return ResponseEntity.ok(standardFees1);
    }

    @DeleteMapping("/deleteStandardFees/{sfid}")
    public ResponseEntity<StandardFeesRequestDTO> deleteStandardFees(@RequestParam String role, @RequestParam String email,@PathVariable Long sfid)
    {
        standardFeesService.deleteStandardFee(role, email, sfid);
        return ResponseEntity.ok().build();
    }


}
