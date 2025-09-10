package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.AssignmentResponseDTO;
import Layer.NewStudentManagement.Entity.StudentAssignment;
import Layer.NewStudentManagement.Service.AssignmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
public class AssignmentController
{

    @Autowired
    AssignmentService assignmentService;

    @PostMapping(value = "/createAssignment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AssignmentResponseDTO createAssignment(@RequestParam String role,
                                                  @RequestParam String email,
                                                  @RequestPart("assignment") String assignmentJson,
                                                  @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // 👈 add this
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 👈 prevents timestamp format

        StudentAssignment assignment = mapper.readValue(assignmentJson, StudentAssignment.class);

        return assignmentService.createAssignment(role, email, image, assignment);
    }

    @PutMapping("/updateAssignment/{id}")
    public AssignmentResponseDTO updateAssignment(@PathVariable Long id,
                                                  @RequestParam String role,
                                                  @RequestParam String email,
                                                  @RequestBody StudentAssignment assignment) {
        return assignmentService.updateAssignment(id, role, email, assignment);
    }

    @DeleteMapping("/deleteAssignment/{id}")
    public String deleteAssignment(@PathVariable Long id,
                                   @RequestParam String role,
                                   @RequestParam String email) {
        assignmentService.deleteAssignment(id, role, email);
        return "Assignment deleted successfully.";
    }

    @GetMapping("/getAssignmentById/{id}")
    public AssignmentResponseDTO getAssignmentById(@PathVariable Long id,@RequestParam String role,
                                                   @RequestParam String email) {
        return assignmentService.getAssignmentById(id, role, email);
    }

    @GetMapping("/getAssignmentByClassroom/{classRoomId}")
    public List<AssignmentResponseDTO> getAssignmentsByClassRoom(@PathVariable Long classRoomId,@RequestParam String role,
                                                                 @RequestParam String email) {
        return assignmentService.getAssignmentsByClassRoom(classRoomId,role,email);
    }

    @GetMapping("/getAssignmentByTeacher")
    public List<AssignmentResponseDTO> getAssignmentsByCreator(@RequestParam String role,
                                                               @RequestParam String email) {
        return assignmentService.getAssignmentsByCreator(role, email);
    }
}
