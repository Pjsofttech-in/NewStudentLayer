package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.*;
import Layer.NewStudentManagement.Entity.StudentDocument;
import Layer.NewStudentManagement.Entity.StudentEntity;
import Layer.NewStudentManagement.Service.S3Service;
import Layer.NewStudentManagement.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class StudentController
{
    @Autowired
    StudentService studentService;

    @Autowired
    S3Service s3Service;

    @PostMapping("/createStudent")
    public ResponseEntity<StudentResponseDTO> saveStudent(@RequestParam String role,
                                                     @RequestParam String email,
                                                     @RequestBody StudentRequest request)
    {
        StudentResponseDTO savedStudent = studentService.saveStudent(role, email, request);
        return ResponseEntity.ok(savedStudent);
    }

    @GetMapping("/getStudentById/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id,
                                                     @RequestParam String role,
                                                     @RequestParam String email) {
        return ResponseEntity.ok(studentService.getStudentById(id, role, email));
    }

    @PostMapping("/getAllStudents")
    public ResponseEntity<Page<StudentResponseDTO>> getFilteredStudents(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam(required = false) String timeFrame,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate customStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate customEnd,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody(required = false) StudentFilterDTO filterDTO
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StudentResponseDTO> students = studentService.getAllStudent(
                role, email, filterDTO, timeFrame, customStart, customEnd, pageable
        );
        return ResponseEntity.ok(students);
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
            @RequestParam(required = false) MultipartFile studentSignPhoto) {

        StudentDocumentDTO saved = studentService.uploadStudentDocuments(
                studentId, role, email,
                studentPhoto, aadharcardPhoto, pancardPhoto,
                casteValidationPhoto, casteCertificatePhoto,
                leavingCertificatePhoto, domicilePhoto,
                birthCertificatePhoto, disabilityCertificate,
                studentSignPhoto
        );
        return ResponseEntity.ok(saved);
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

    @GetMapping("/getStudentforAssignToClassRoom")
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
            @RequestParam Long studentId, @RequestParam String status) {
        studentService.updateStatus(role,email,studentId, status);
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


}
