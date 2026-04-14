package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StudentPeriodResponseDTO;
import Layer.NewStudentManagement.Entity.StudentPeriod;
import Layer.NewStudentManagement.Service.PeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
public class PeriodController
{

    @Autowired
    PeriodService periodService;

    @PostMapping("/createPeriod")
    public StudentPeriodResponseDTO createPeriod(
            @RequestParam String role,
            @RequestParam String email,
            @RequestBody StudentPeriod period) {
        return periodService.createPeriod(role, email, period);
    }

    @GetMapping("/getPeriodById/{id}")
    public StudentPeriodResponseDTO getById(
            @RequestParam String role,
            @RequestParam String email,
            @PathVariable Long id) {
        return periodService.getPeriodById(role, email, id);
    }

    @GetMapping("/getAllPeriod")
    public List<StudentPeriodResponseDTO> getAll(
            @RequestParam String role,
            @RequestParam String email) {
        return periodService.getAllPeriod(role, email);
    }

    @PutMapping("/updatePeriod/{id}")
    public StudentPeriodResponseDTO update(
            @RequestParam String role,
            @RequestParam String email,
            @PathVariable Long id,
            @RequestBody StudentPeriod period) {
        return periodService.updatePeriod(role, email, id, period);
    }

    @DeleteMapping("/deletePeriod/{id}")
    public String delete(
            @RequestParam String role,
            @RequestParam String email,
            @PathVariable Long id) {
        periodService.deletePeriod(role, email, id);
        return "Period deleted successfully with ID: " + id;
    }
}
