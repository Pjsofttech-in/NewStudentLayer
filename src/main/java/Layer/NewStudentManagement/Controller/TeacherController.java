package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.*;
import Layer.NewStudentManagement.Entity.StudentTeacher;
import Layer.NewStudentManagement.Repository.TeacherRepository;
import Layer.NewStudentManagement.Security.LoginRequest;
import Layer.NewStudentManagement.Security.LoginResponse;
import Layer.NewStudentManagement.Service.TeacherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class TeacherController
{
    @Autowired
    private TeacherService teacherService;

    @PostMapping(value = "/createTeacher", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentTeacherDTO> createTeacher(
            @RequestPart("dto") String dtoJson,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestParam String email,
            @RequestParam String role
    ) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // support LocalDate
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        TeacherRequestDTO dto = objectMapper.readValue(dtoJson, TeacherRequestDTO.class);

        StudentTeacherDTO savedTeacher = teacherService.createTeacher(role,email,image,dto);

        return ResponseEntity.ok(savedTeacher);
    }


    @GetMapping("/getTeacherById/{id}")
    public ResponseEntity<StudentTeacherDTO> getTeacherById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        StudentTeacherDTO teacher = teacherService.getTeacherById(id,role,email);
        return ResponseEntity.ok(teacher);
    }

    @PutMapping(value = "/updateTeacher/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentTeacherDTO> updateTeacher(
            @PathVariable("id") Long id,
            @RequestParam("role") String role,
            @RequestParam("email") String email,
            @RequestPart(value = "profilePhoto", required = false) MultipartFile profilePhoto,
            @RequestPart("dto") String dtoJson
    ) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        TeacherRequestDTO teacherDTO = objectMapper.readValue(dtoJson, TeacherRequestDTO.class);

        StudentTeacherDTO updatedTeacher = teacherService.updateTeacher(id, role, email, profilePhoto, teacherDTO);

        return ResponseEntity.ok(updatedTeacher);
    }

    @DeleteMapping("/deleteTeacher/{id}")
    public ResponseEntity<Void> deleteTeacherById(@PathVariable Long id, @RequestParam String role, @RequestParam String email)
    {
        teacherService.deleteTeacherById(id,role,email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAllTeacher")
    public ResponseEntity<Iterable<StudentTeacherDTO>> getAllTeacher(@RequestParam String role, @RequestParam String email)
    {
        Iterable<StudentTeacherDTO> teachers = teacherService.getAllTeacher(role,email);
        return ResponseEntity.ok(teachers);
    }

//    @GetMapping("/getTeacherByInstitutionType")
//    public ResponseEntity<Iterable<StudentTeacherDTO>> getTeacherByInstitutionType(@RequestParam String role, @RequestParam String email, @RequestParam String institutionType)
//    {
//        Iterable<StudentTeacherDTO> teachers = teacherService.getTeacherByInstitutionType(role,email,institutionType);
//        return ResponseEntity.ok(teachers);
//    }

    @PostMapping("/teacherLogin")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = teacherService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sendOtpToTeacher")
    public ResponseEntity<String> sendOtp(@RequestBody SendOtpRequest request) {
        return ResponseEntity.ok(teacherService.sendOtp(request.getEmail()));
    }

    @PostMapping("/verifyOtpToTeacher")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(teacherService.verifyOtp(request.getEmail(), request.getOtp()));
    }

    @PostMapping("/resetTeacherPassword")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(teacherService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword()));
    }

    @GetMapping("/teacherLogout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/getTeacherByInstitutionType")
    public ResponseEntity<List<StudentTeacherDTO>> getTeachers(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam(required = false) String institutionType,
            @RequestParam(required = false) String graduationType,
            @RequestParam(required = false) String stream,
            @RequestParam(required = false) String degreeName,
            @RequestParam(required = false) String departmentName
    ) {
        if (institutionType == null || institutionType.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        List<StudentTeacherDTO> teachers = teacherService.getTeachers
                (role, email, institutionType, graduationType,stream, degreeName,
                departmentName);
        return ResponseEntity.ok(teachers);
    }


}
