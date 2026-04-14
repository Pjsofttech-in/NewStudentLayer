package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.*;
import Layer.NewStudentManagement.Entity.StudentDocument;
import Layer.NewStudentManagement.Entity.StudentEntity;
import Layer.NewStudentManagement.Repository.StudentRepository;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Security.LoginRequest;
import Layer.NewStudentManagement.Security.LoginResponse;
import Layer.NewStudentManagement.Service.S3Service;
import Layer.NewStudentManagement.Service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class StudentController
{
    @Autowired
    StudentService studentService;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    S3Service s3Service;

    @PostMapping("/createStudent")
    public ResponseEntity<StudentResponseDTO> saveStudent(@RequestParam String role,
                                                     @RequestParam(required = false) String email,
                                                     @RequestBody StudentRequest request,
                                                     @RequestHeader(name = "Authorization", required = false) String authorizationHeader)
    {
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        StudentResponseDTO saved = studentService.saveStudent(role, email,request,token);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/getStudentById/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id,
                                                     @RequestParam String role,
                                                     @RequestParam String email) {
        return ResponseEntity.ok(studentService.getStudentById(id, role, email));
    }

    @PostMapping("/getAllStudents")
    public ResponseEntity<Map<String, Object>> getFilteredStudents(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam(required = false) String timeFrame,
            @RequestParam(required = false) String staffEmail,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate customStart,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate customEnd,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestBody(required = false) StudentFilterDTO filterDTO
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<StudentResponseDTO> studentPage =
                studentService.getAllStudent(
                        role, email, staffEmail,
                        filterDTO, timeFrame, customStart, customEnd, pageable
                );

        Map<String, Object> response = new HashMap<>();
        response.put("content", studentPage.getContent());
        response.put("totalStudentCount", studentPage.getTotalElements());
        response.put("totalPages", studentPage.getTotalPages());
        response.put("page", studentPage.getNumber());
        response.put("size", studentPage.getSize());
        response.put("first", studentPage.isFirst());
        response.put("last", studentPage.isLast());

        return ResponseEntity.ok(response); // ✅ FIXED
    }



    @PutMapping("/updateStudent/{id}")
    public ResponseEntity<StudentResponseDTO> updateStudent(@PathVariable Long id,
                                                            @RequestParam String role,
                                                            @RequestParam String email,
                                                            @RequestBody StudentRequest request) {
        return ResponseEntity.ok(studentService.updateStudent(id, role, email, request));
    }

    @DeleteMapping("/deleteStudent/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id,
                                                @RequestParam String role,
                                                @RequestParam String email) {
        studentService.deleteStudentById(id, role, email);
        return ResponseEntity.ok("Student deleted successfully with ID: " + id);
    }

    @PostMapping("/uploadDocument")
    public ResponseEntity<StudentDocumentDTO> uploadDocuments(
            @RequestParam Long studentId,
            @RequestParam String role,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) MultipartFile studentPhoto,
            @RequestParam(required = false) MultipartFile aadharcardPhoto,
            @RequestParam(required = false) MultipartFile pancardPhoto,
            @RequestParam(required = false) MultipartFile casteValidationPhoto,
            @RequestParam(required = false) MultipartFile casteCertificatePhoto,
            @RequestParam(required = false) MultipartFile leavingCertificatePhoto,
            @RequestParam(required = false) MultipartFile domicilePhoto,
            @RequestParam(required = false) MultipartFile birthCertificatePhoto,
            @RequestParam(required = false) MultipartFile disabilityCertificate,
            @RequestParam(required = false) MultipartFile studentSignPhoto,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader)
    {
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);  // Extract token after "Bearer "
        }

        StudentDocumentDTO academicYear = studentService.uploadStudentDocuments(studentId, role, email,
                    studentPhoto, aadharcardPhoto, pancardPhoto, casteValidationPhoto, casteCertificatePhoto,
                    leavingCertificatePhoto, domicilePhoto, birthCertificatePhoto, disabilityCertificate, studentSignPhoto,token);
            return ResponseEntity.ok(academicYear);

    }

    @PutMapping("/updateDocument")
    public ResponseEntity<StudentDocumentDTO> updateDocuments(
            @RequestParam Long studentId,
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam(required = false) MultipartFile studentPhoto,
            @RequestParam(required = false) MultipartFile aadharcardPhoto,
            @RequestParam(required = false) MultipartFile pancardPhoto,
            @RequestParam(required = false) MultipartFile casteValidationPhoto,
            @RequestParam(required = false) MultipartFile casteCertificatePhoto,
            @RequestParam(required = false) MultipartFile leavingCertificatePhoto,
            @RequestParam(required = false) MultipartFile domicilePhoto,
            @RequestParam(required = false) MultipartFile birthCertificatePhoto,
            @RequestParam(required = false) MultipartFile disabilityCertificate,
            @RequestParam(required = false) MultipartFile studentSignPhoto
    ) {
        StudentDocumentDTO updated = studentService.updateStudentDocuments(
                studentId, role, email,
                studentPhoto, aadharcardPhoto, pancardPhoto,
                casteValidationPhoto, casteCertificatePhoto,
                leavingCertificatePhoto, domicilePhoto,
                birthCertificatePhoto, disabilityCertificate,
                studentSignPhoto
        );
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/getStudentforAssignToClassRoom")
    public ResponseEntity<Page<StudentResponseDTO>> filterStudentsForClassroom(
            @RequestParam String role,
            @RequestParam String email,
            @RequestBody StudentClassRoomFilterDTO filterDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StudentResponseDTO> students = studentService.filterStudentsForClassroom(role, email, filterDTO, pageable);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/getStudentByClassRoomId")
    public ResponseEntity<List<StudentResponseDTO>> getStudentsByClassRoomId(@RequestParam String role, @RequestParam String email,@RequestParam Long classRoomId) {
        List<StudentResponseDTO> students = studentService.getStudentsByClassRoomId(role , email,classRoomId);
        return ResponseEntity.ok(students);
    }


    @PutMapping("/updateStudentStatus")
    public ResponseEntity<String> updateStudentStatus(
            @RequestParam String role, @RequestParam String email,
            @RequestParam Long studentId, @RequestParam String status,
            @RequestParam(required = false) String reason) {
        studentService.updateStatus(role,email,studentId, status,reason);
        return ResponseEntity.ok("Status updated successfully");
    }

    @GetMapping("/getStudentByRegistrationNumber")
    public ResponseEntity<StudentDTO> getStudentByRegistrationNumber(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String registrationNumber
    ) {
        StudentDTO student = studentService.getStudentByRegistrationNumber(role, email, registrationNumber);
        return ResponseEntity.ok(student);
    }


    @DeleteMapping("/deleteEducationById")
    public ResponseEntity<String> deleteEducationDetail(@RequestParam String role,@RequestParam String email,@RequestParam Long educationId)
    {
        studentService.deleteEducationById(role, email,educationId);
        return ResponseEntity.ok("Education deleted successfully with ID: " + educationId);
    }

    @PutMapping("/updateStudentFormStatus")
    public ResponseEntity<String> updateStudentFormStatus(
            @RequestParam String role, @RequestParam String email,
            @RequestParam Long studentId) {
        studentService.updateFormStatus(role,email,studentId);
        return ResponseEntity.ok("Status updated successfully");
    }

    @PostMapping("/studentLogin")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = studentService.studentLogin(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/getDataForTc")
    public ResponseEntity<?> getDataForTc(@RequestParam Long studentId,@RequestParam String role, @RequestParam String email) {
        try {
            DataForTcDTO dto = studentService.getDataForTc(studentId,role,email);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        }
    }


    @PostMapping("/getAllStudentRequest")
    public StudentPageResponseDTO getStudentsByBranchCode(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam(required = false) String timeFrame,
            @RequestParam(required = false) LocalDate customStart,
            @RequestParam(required = false) LocalDate customEnd,
            @RequestBody(required = false) StudentFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        return studentService.getStudentsByBranchCode(role, email, filter, timeFrame,
                customStart, customEnd, pageable);
    }


    @PostMapping(value = "/registerStudent", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentResponseDTO> registerStudent(
            @RequestParam String role,
            @RequestParam(required = false) String email,
            @RequestPart("student") String studentJson,
            @RequestPart(value = "oldRegisterPhoto", required = false) MultipartFile oldRegisterPhoto,
            @RequestPart(value = "entranceMarkSheet", required = false) MultipartFile entranceMarkSheet,
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader) {

        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules(); // handles LocalDate
            StudentRegisterRequest request = mapper.readValue(studentJson, StudentRegisterRequest.class);

            StudentResponseDTO response = studentService.registerStudent(role, email, request, oldRegisterPhoto, entranceMarkSheet,token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse student JSON or register student", e);
        }
    }

    @GetMapping("/getStaffInfoByBranchCode")
    public List<Map<String, Object>> getStaffBasicInfo(
            @RequestParam(required = false) String deptEmail,
            @RequestParam String role,
            @RequestParam String email) {

        return studentService.getStaffInfo(role, email,deptEmail);
    }

}
