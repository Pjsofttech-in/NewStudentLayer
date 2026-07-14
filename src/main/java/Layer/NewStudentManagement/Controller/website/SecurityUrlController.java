package Layer.NewStudentManagement.Controller.website;

import Layer.NewStudentManagement.Entity.website.StudentWebSecurityUrl;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.website.SecurityUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "https://pjsofttech.in")
public class SecurityUrlController {

    @Autowired
    private SecurityUrlService service;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/createSecurityUrl")
    public ResponseEntity<StudentWebSecurityUrl> create(@RequestBody StudentWebSecurityUrl webSecurityUrl,
                                                 @RequestParam String role,
                                                 @RequestParam String email) {
        return ResponseEntity.ok(service.create(webSecurityUrl,role,email));
    }

    @GetMapping("/getAllSecurityUrls")
    public ResponseEntity<List<StudentWebSecurityUrl>> getAllByBranchCode(@RequestParam String role,
                                                                   @RequestParam(required = false) String email,
                                                                   @RequestParam String branchCode) {
        return ResponseEntity.ok(service.getAllByBranchCode(role, email, branchCode));
    }
    @PutMapping("/updateSecurityUrl/{id}")
    public ResponseEntity<StudentWebSecurityUrl> update(@PathVariable long id,
                                                 @RequestBody StudentWebSecurityUrl webSecurityUrl,
                                                 @RequestParam String role,
                                                 @RequestParam String email) {
        return ResponseEntity.ok(service.update(id, webSecurityUrl, role, email));
    }

    @GetMapping("/getTokenForUser")
    public ResponseEntity<?> generateTokenByUrl(@RequestParam String url) {
        String token = jwtUtil.generateTokenFromUrl(url);
        String branchCode = service.getBranchCodeByUrl(url);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("branchCode", branchCode);

        return ResponseEntity.ok(response);
    }

}