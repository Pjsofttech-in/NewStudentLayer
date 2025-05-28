package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.Entity.StudentGroup;
import Layer.NewStudentManagement.Service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class GroupController
{
    @Autowired
    private GroupService groupService;


    @PostMapping("/createGroup")
    public ResponseEntity<StudentGroup> createGroup(@RequestParam String role, @RequestParam String email, @RequestBody StudentGroup group)
    {
        StudentGroup createdGroup = groupService.createGroup(role,email,group);
        return ResponseEntity.ok(createdGroup);
    }

    @GetMapping("/getGroupById/{id}")
    public ResponseEntity<StudentGroup> getGroupById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        StudentGroup group = groupService.getGroupById(id,role,email);
        return ResponseEntity.ok(group);
    }

    @GetMapping("/getAllGroup")
    public ResponseEntity<Iterable<StudentGroup>> getAllGroup(@RequestParam String role, @RequestParam String email)
    {
        Iterable<StudentGroup> groups = groupService.getAllGroupByName(role, email);
        return ResponseEntity.ok(groups);
    }

    @PutMapping("/updateGroup/{id}")
    public ResponseEntity<StudentGroup> updateGroup(@PathVariable Long id, @RequestParam String role, @RequestParam String email, @RequestBody StudentGroup group)
    {
        StudentGroup updatedGroup = groupService.updateGroup(id,role,email,group);
        return ResponseEntity.ok(updatedGroup);
    }

    @DeleteMapping("/deleteGroup/{id}")
    public ResponseEntity<Void> deleteGroupById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        groupService.deleteGroupById(id,role,email);
        return ResponseEntity.ok().build();
    }


}
