package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StudentDepartmentDTO;
import Layer.NewStudentManagement.Entity.StudentDepartment;
import Layer.NewStudentManagement.Service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;

//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class DepartmentController
{
    @Autowired
    DepartmentService departmentService;


    @PostMapping("/createDepartment")
    public ResponseEntity<StudentDepartmentDTO> createDepartment(@RequestParam String role, @RequestParam String email, @RequestBody StudentDepartmentDTO department)
    {
        StudentDepartmentDTO createDepartment = departmentService.saveDepartment(role,email,department);
        return ResponseEntity.ok(createDepartment);
    }

    @GetMapping("/getAllDepartment")
    public ResponseEntity<Iterable<StudentDepartmentDTO>> getAllDepartment(@RequestParam String role, @RequestParam String email)
    {
        Iterable<StudentDepartmentDTO> department = departmentService.getAllDepartment(role,email);
        return ResponseEntity.ok(department);
    }
    @GetMapping("/getDepartmentById/{id}")
    public ResponseEntity<StudentDepartmentDTO> getDepartmentById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        StudentDepartmentDTO department = departmentService.getDepartmentById(id,role,email);
        return ResponseEntity.ok(department);
    }

    @PutMapping("/updateDepartment/{id}")
    public ResponseEntity<StudentDepartmentDTO> updateDepartment(@PathVariable Long id, @RequestParam String role, @RequestParam String email, @RequestBody StudentDepartment department)
    {
        StudentDepartmentDTO updatedDepartment = departmentService.updateDepartment(id,role,email,department);
        return ResponseEntity.ok(updatedDepartment);
    }

    @DeleteMapping("/deleteDepartment/{id}")
    public ResponseEntity<Void> deleteDepartmentById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        departmentService.deleteDepartmentById(id,role,email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getDepartmentByDegree")
    public ResponseEntity<List<StudentDepartmentDTO>> getDepartmentsByDegreeId(
            @RequestParam String role,
            @RequestParam(required = false) String email,
            @RequestParam Long degreeId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader)
    {
        try {
            String token = null;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);  // Extract token after "Bearer "
            }

            List<StudentDepartmentDTO> department = departmentService.getDepartmentsByDegreeId(role, email, degreeId,token);
            return ResponseEntity.ok(department);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
        }
    }


}
