package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StreamDTO;
import Layer.NewStudentManagement.Entity.StudentStream;
import Layer.NewStudentManagement.Service.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class StreamController
{

    @Autowired
    private StreamService streamService;

    @PostMapping("/createStream")
    public ResponseEntity<StudentStream> createStream(@RequestParam String role, @RequestParam String email, @RequestBody StudentStream stream)
    {
        StudentStream createdStream = streamService.createStream(role,email,stream);
        return ResponseEntity.ok(createdStream);
    }

    @GetMapping("/getStreamById/{id}")
    public ResponseEntity<StreamDTO> getStreamById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        StreamDTO stream = streamService.getStreamById(id,role,email);
        return ResponseEntity.ok(stream);
    }

    @GetMapping("/getAllStream")
    public ResponseEntity<Iterable<StreamDTO>> getAllStream(@RequestParam String role, @RequestParam(required = false) String email,
                                                            @RequestHeader(value = "Authorization", required = false) String authorizationHeader)
    {
        try {
            String token = null;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);  // Extract token after "Bearer "
            }

            List<StreamDTO> academicYear = streamService.getAllStream(role, email, token);
            return ResponseEntity.ok(academicYear);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
        }
    }

    @PutMapping("/updateStream/{id}")
    public ResponseEntity<StreamDTO> updateStream(@PathVariable Long id, @RequestParam String role, @RequestParam String email, @RequestBody StudentStream stream)
    {
        StreamDTO updatedStream = streamService.updateStream(id,role,email,stream);
        return ResponseEntity.ok(updatedStream);
    }

    @DeleteMapping("/deleteStream/{id}")
    public ResponseEntity<Void> deleteStreamById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        streamService.deleteStreamById(id,role,email);
        return ResponseEntity.ok().build();
    }
}
