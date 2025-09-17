package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.AssignmentSubmissionResponseDTO;
import Layer.NewStudentManagement.Entity.StudentAssignmentSubmission;
import Layer.NewStudentManagement.Service.AssignmentSubmissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
public class AssignmentSubmissionController
{

    @Autowired
    AssignmentSubmissionService submissionService;

    @PostMapping(value = "/submitAssignmentByStudent", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AssignmentSubmissionResponseDTO submitAssignment(
            @RequestParam String role,
            @RequestParam String email,
            @RequestPart("submission") String submissionJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception
    {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // 👈 add this
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        StudentAssignmentSubmission assignmentSubmission = mapper.readValue(submissionJson, StudentAssignmentSubmission.class);
        return submissionService.submitAssignment(role, email, file, assignmentSubmission);
    }

    @GetMapping("/getSubmissionById/{id}")
    public AssignmentSubmissionResponseDTO getSubmission(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        return submissionService.getSubmission(id, role, email);
    }

    @PutMapping("/updateSubmission/{id}")
    public AssignmentSubmissionResponseDTO updateSubmission(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email,
            @RequestPart("submission") String submissionJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception
    {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        StudentAssignmentSubmission assignmentSubmission = mapper.readValue(submissionJson, StudentAssignmentSubmission.class);

        return submissionService.updateSubmission(role, email, id, file,assignmentSubmission);
    }

    @DeleteMapping("/deleteAssignmentSubmission/{id}")
    public String deleteSubmission(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email) {
        submissionService.deleteSubmission(id,role, email);
        return "Submission deleted successfully!";
    }

    @GetMapping("/getAllSubmittedAssignmentByClassroom")
    public List<AssignmentSubmissionResponseDTO> getByClassRoom(
            @RequestParam Long classRoomId,
            @RequestParam String role,
            @RequestParam String email) {
        return submissionService.getSubmissionsByClassRoom(classRoomId, role, email);
    }


    @GetMapping("/getAllSubmittedAssignmentByStudent")
    public ResponseEntity<List<AssignmentSubmissionResponseDTO>> getByStudent(
            @RequestParam Long studentId,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam(required = false) String filter) {
        return ResponseEntity.ok(submissionService.getSubmissionsByStudent(studentId, role, email, filter));
    }

    @GetMapping("/getSubmissionsByAssignmentId")
    public ResponseEntity<List<AssignmentSubmissionResponseDTO>> getSubmissionsByAssignmentId(@RequestParam Long assignmentId,
                                                                          @RequestParam String role,
                                                                          @RequestParam String email) {
        return ResponseEntity.ok(submissionService.getSubmissionsByAssignmentId(assignmentId, role, email));
    }

}
