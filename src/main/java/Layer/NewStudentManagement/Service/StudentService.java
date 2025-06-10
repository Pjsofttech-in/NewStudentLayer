package Layer.NewStudentManagement.Service;


import Layer.NewStudentManagement.DTO.StudentDTO;
import Layer.NewStudentManagement.DTO.StudentDocumentDTO;
import Layer.NewStudentManagement.DTO.StudentRequest;
import Layer.NewStudentManagement.DTO.StudentResponseDTO;
import Layer.NewStudentManagement.Entity.StudentDocument;
import Layer.NewStudentManagement.Entity.StudentEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentService
{
    StudentEntity saveStudent(String role, String email, StudentRequest request);
    StudentDTO getStudentById(Long id, String role, String email);
    StudentResponseDTO updateStudent(Long id, String role, String email, StudentRequest request);
    void deleteStudentById(Long id,String role,String email);
    List<StudentResponseDTO> getAllStudent(String role, String email);
    StudentDocumentDTO uploadStudentDocuments(Long studentId, String role, String email, MultipartFile studentPhoto, MultipartFile aadharcardPhoto, MultipartFile pancardPhoto,
                                              MultipartFile casteValidationPhoto, MultipartFile casteCertificatePhoto, MultipartFile leavingCertificatePhoto, MultipartFile domicilePhoto,
                                              MultipartFile birthCertificatePhoto, MultipartFile disabilityCertificate, MultipartFile studentSignPhoto);

    StudentDocumentDTO updateStudentDocuments(Long studentId, String role, String email,
                                              MultipartFile studentPhoto, MultipartFile aadharcardPhoto, MultipartFile pancardPhoto,
                                              MultipartFile casteValidationPhoto, MultipartFile casteCertificatePhoto,
                                              MultipartFile leavingCertificatePhoto, MultipartFile domicilePhoto,
                                              MultipartFile birthCertificatePhoto, MultipartFile disabilityCertificate, MultipartFile studentSignPhoto);

    List<StudentResponseDTO> getStudentByMediumDivisionStandard(String role, String email ,String medium, String standard,String year);

    List<StudentResponseDTO> getStudentsByClassRoomId(String role, String email,Long classRoomId);
}
