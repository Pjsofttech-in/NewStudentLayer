package Layer.NewStudentManagement.Service;


import Layer.NewStudentManagement.DTO.*;
import Layer.NewStudentManagement.Entity.StudentDocument;
import Layer.NewStudentManagement.Entity.StudentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StudentService
{
    StudentResponseDTO  saveStudent(String role, String email, StudentRequest request,String token);
    StudentDTO getStudentById(Long id, String role, String email);
    StudentResponseDTO updateStudent(Long id, String role, String email, StudentRequest request);
    void deleteStudentById(Long id,String role,String email);
    Page<StudentResponseDTO> getAllStudent(String role, String email, StudentFilterDTO filter,
                                           String timeFrame, LocalDate customStart, LocalDate customEnd,
                                           Pageable pageable);
    StudentDocumentDTO uploadStudentDocuments(Long studentId, String role, String email, MultipartFile studentPhoto, MultipartFile aadharcardPhoto, MultipartFile pancardPhoto,
                                              MultipartFile casteValidationPhoto, MultipartFile casteCertificatePhoto, MultipartFile leavingCertificatePhoto, MultipartFile domicilePhoto,
                                              MultipartFile birthCertificatePhoto, MultipartFile disabilityCertificate, MultipartFile studentSignPhoto);

    StudentDocumentDTO updateStudentDocuments(Long studentId, String role, String email,
                                              MultipartFile studentPhoto, MultipartFile aadharcardPhoto, MultipartFile pancardPhoto,
                                              MultipartFile casteValidationPhoto, MultipartFile casteCertificatePhoto,
                                              MultipartFile leavingCertificatePhoto, MultipartFile domicilePhoto,
                                              MultipartFile birthCertificatePhoto, MultipartFile disabilityCertificate, MultipartFile studentSignPhoto);

    Page<StudentResponseDTO> filterStudentsForClassroom(String role, String email, StudentClassRoomFilterDTO filterDTO, Pageable pageable);

    List<StudentResponseDTO> getStudentsByClassRoomId(String role, String email,Long classRoomId);

    void updateStatus(String role, String email,Long studentId, String status,String reason);

    void updateFormStatus(String role, String email, Long studentId);

    StudentDTO getStudentByRegistrationNumber(String role, String email, String registrationNumber);

    void deleteEducationById(String role, String email, Long educationId);

    Map<String, Long> getApplicationCount(String filter, LocalDate customStart, LocalDate customEnd);
}
