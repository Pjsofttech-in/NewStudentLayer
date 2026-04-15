package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.StudentGroupDTO;
import Layer.NewStudentManagement.Entity.StudentGroup;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class GroupController
{
    @Autowired
    private GroupService groupService;

    @Autowired
    private JwtUtil jwtUtil;


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
    public ResponseEntity<?> getAllGroup(
            @RequestParam String role,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String branchCode,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {

        try {
            // ✅ Validate role
            if (role == null || role.isBlank()) {
                return ResponseEntity.badRequest().body("Role is required");
            }

            String tokenEmail = null;

            // ✅ Extract email from token
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                tokenEmail = jwtUtil.extractEmail(token);
            }

            // ✅ Priority: param email > token email
            String finalEmail = (email != null && !email.isBlank()) ? email : tokenEmail;

            if (finalEmail == null || finalEmail.isBlank()) {
                return ResponseEntity.badRequest().body("Email not found");
            }

            List<StudentGroupDTO> result =
                    groupService.getAllGroup(role, finalEmail, branchCode);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
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
