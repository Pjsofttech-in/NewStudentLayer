package Layer.NewStudentManagement.Controller;


import Layer.NewStudentManagement.Entity.StudentWebsiteHeaders;
import Layer.NewStudentManagement.Service.WebsiteHeaderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/website/headers/")
public class StudentWebsiteHeaderController {

    private final WebsiteHeaderService service;

    public StudentWebsiteHeaderController(WebsiteHeaderService service) {
        this.service = service;
    }

    @GetMapping("/tree")
    public ResponseEntity<List<StudentWebsiteHeaders>> getMenuTree() {
        return ResponseEntity.ok(service.getMenuTree());
    }

    @GetMapping
    public ResponseEntity<List<StudentWebsiteHeaders>> getAllLinks() {
        return ResponseEntity.ok(service.getAllLinks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentWebsiteHeaders> getLinkById(@PathVariable Long id) {
        return service.getLinkById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StudentWebsiteHeaders> createLink(@RequestBody StudentWebsiteHeaders newLink) {
        try {
            StudentWebsiteHeaders createdLink = service.createLink(newLink);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLink);
        } catch (IllegalArgumentException e) {
            // Returns a 400 Bad Request if the user passed a bad parent_id
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentWebsiteHeaders> updateLink(@PathVariable Long id, @RequestBody StudentWebsiteHeaders updatedData) {
        try {
            return service.updateLink(id, updatedData)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLink(@PathVariable Long id) {
        if (service.deleteLink(id)) {
            return ResponseEntity.noContent().build(); // 204 No Content for successful deletion
        }
        return ResponseEntity.notFound().build(); // 404 Not Found if ID didn't exist
    }
}
