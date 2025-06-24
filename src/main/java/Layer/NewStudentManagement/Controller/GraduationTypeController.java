package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StudentGraduationTypeDTO;
import Layer.NewStudentManagement.Entity.StudentGraduationType;
import Layer.NewStudentManagement.Service.GraduationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GraduationTypeController
{

    @Autowired
    GraduationTypeService graduationTypeService;


    @PostMapping("/createGraduationType")
    public ResponseEntity<StudentGraduationTypeDTO> createGraduationType(@RequestParam String role, @RequestParam String email, @RequestBody StudentGraduationTypeDTO graduationType)
    {
        StudentGraduationTypeDTO createGraduationType = graduationTypeService.saveGraduationType(role,email,graduationType);
        return ResponseEntity.ok(createGraduationType);
    }

    @GetMapping("/getAllGraduationType")
    public ResponseEntity<Iterable<StudentGraduationTypeDTO>> getAllGraduationType(@RequestParam String role, @RequestParam String email)
    {
        Iterable<StudentGraduationTypeDTO> graduationType = graduationTypeService.getAllGraduationType(role,email);
        return ResponseEntity.ok(graduationType);
    }
    @GetMapping("/getGraduationTypeById/{id}")
    public ResponseEntity<StudentGraduationTypeDTO> getGraduationTypeById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        StudentGraduationTypeDTO graduationType = graduationTypeService.getGraduationTypeById(id,role,email);
        return ResponseEntity.ok(graduationType);
    }

    @PutMapping("/updateGraduationType/{id}")
    public ResponseEntity<StudentGraduationTypeDTO> updateGraduationType(@PathVariable Long id, @RequestParam String role, @RequestParam String email, @RequestBody StudentGraduationType graduationType)
    {
        StudentGraduationTypeDTO updatedGraduationType = graduationTypeService.updateGraduationType(id,role,email,graduationType);
        return ResponseEntity.ok(updatedGraduationType);
    }

    @DeleteMapping("/deleteGraduationType/{id}")
    public ResponseEntity<Void> deleteGraduationTypeById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        graduationTypeService.deleteGraduationTypeById(id,role,email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/graduationTypesByStreamName")
    public ResponseEntity<List<StudentGraduationTypeDTO>> getGraduationTypesByStream(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String streamName) {
        return ResponseEntity.ok(
                graduationTypeService.getGraduationTypesByStream(role, email, streamName)
        );
    }


}
