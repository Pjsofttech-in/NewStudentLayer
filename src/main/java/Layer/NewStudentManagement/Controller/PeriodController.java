package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StudentPeriodResponseDTO;
import Layer.NewStudentManagement.Entity.StudentPeriod;
import Layer.NewStudentManagement.Service.PeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
public class PeriodController
{

    @Autowired
    PeriodService periodService;

    @PostMapping("/createPeriod")
    public StudentPeriodResponseDTO create(
            @RequestParam String role,
            @RequestParam String email,
            @RequestBody StudentPeriod period) {
        return periodService.create(role, email, period);
    }

    @GetMapping("/getPeriodById/{id}")
    public StudentPeriodResponseDTO getById(
            @RequestParam String role,
            @RequestParam String email,
            @PathVariable Long id) {
        return periodService.getById(role, email, id);
    }

    @GetMapping("/getAllPeriod")
    public List<StudentPeriodResponseDTO> getAll(
            @RequestParam String role,
            @RequestParam String email) {
        return periodService.getAll(role, email);
    }

    @PutMapping("/updatePeriod/{id}")
    public StudentPeriodResponseDTO update(
            @RequestParam String role,
            @RequestParam String email,
            @PathVariable Long id,
            @RequestBody StudentPeriod period) {
        return periodService.update(role, email, id, period);
    }

    @DeleteMapping("/deletePeriod/{id}")
    public String delete(
            @RequestParam String role,
            @RequestParam String email,
            @PathVariable Long id) {
        periodService.delete(role, email, id);
        return "Period deleted successfully with ID: " + id;
    }
}
