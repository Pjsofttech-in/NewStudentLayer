package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StudentGroupDTO;
import Layer.NewStudentManagement.Entity.StudentGroup;
import Layer.NewStudentManagement.Service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class GroupController
{
    @Autowired
    private GroupService groupService;


    @PostMapping("/createGroup")
    public ResponseEntity<StudentGroupDTO> createGroup(@RequestParam String role, @RequestParam String email, @RequestBody StudentGroup group)
    {
        StudentGroupDTO createdGroup = groupService.createGroup(role,email,group);
        return ResponseEntity.ok(createdGroup);
    }

    @GetMapping("/getGroupById/{id}")
    public ResponseEntity<StudentGroupDTO> getGroupById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        StudentGroupDTO group = groupService.getGroupById(id,role,email);
        return ResponseEntity.ok(group);
    }

    @GetMapping("/getAllGroup")
    public ResponseEntity<Iterable<StudentGroupDTO>> getAllGroup(@RequestParam String role, @RequestParam(required = false) String email,
                                                                 @RequestHeader(value = "Authorization", required = false) String authorizationHeader)
    {try {
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);  // Extract token after "Bearer "
        }

        List<StudentGroupDTO> academicYear = groupService.getAllGroupByName(role, email, token);
        return ResponseEntity.ok(academicYear);
    } catch (RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
    }
    }

    @PutMapping("/updateGroup/{id}")
    public ResponseEntity<StudentGroupDTO> updateGroup(@PathVariable Long id, @RequestParam String role, @RequestParam String email, @RequestBody StudentGroup group)
    {
        StudentGroupDTO updatedGroup = groupService.updateGroup(id,role,email,group);
        return ResponseEntity.ok(updatedGroup);
    }

    @DeleteMapping("/deleteGroup/{id}")
    public ResponseEntity<Void> deleteGroupById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        groupService.deleteGroupById(id,role,email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getGroupByGraduationType/{id}")
    public ResponseEntity<Iterable<StudentGroupDTO>> getGroupsByGraduationTypeId( @RequestParam String role, @RequestParam String email,@PathVariable Long id)
    {
        Iterable<StudentGroupDTO> groupDTOS = groupService.getGroupsByGraduationTypeId(role,email,id);
        return ResponseEntity.ok(groupDTOS);
    }


}
