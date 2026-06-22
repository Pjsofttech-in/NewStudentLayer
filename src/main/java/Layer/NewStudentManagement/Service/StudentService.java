package Layer.NewStudentManagement.Service;


import Layer.NewStudentManagement.DTO.*;
import Layer.NewStudentManagement.Security.LoginRequest;
import Layer.NewStudentManagement.Security.LoginResponse;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StudentService {
    StudentResponseDTO saveStudent(String role, String email, StudentRequest request, String token);

    StudentDTO getStudentById(Long id, String role, String email);

    String sendOtp(String email, String callType);

    String verifyOtp(String email, String otp, String callType);

    String resetPassword(String email, String otp, String newPassword, String callType);

    StudentResponseDTO updateStudent(Long id, String role, String email, StudentRequest request);

    void deleteStudentById(Long id, String role, String email);

    Page<StudentResponseDTO> getAllStudent(String role, String email, String staffEmail, StudentFilterDTO filter,
                                           String timeFrame, LocalDate customStart, LocalDate customEnd,
                                           Pageable pageable);

    StudentDocumentDTO uploadStudentDocuments(Long studentId, String role, String email, MultipartFile studentPhoto, MultipartFile aadharcardPhoto, MultipartFile pancardPhoto,
                                              MultipartFile casteValidationPhoto, MultipartFile casteCertificatePhoto, MultipartFile leavingCertificatePhoto, MultipartFile domicilePhoto,
                                              MultipartFile birthCertificatePhoto, MultipartFile disabilityCertificate, MultipartFile studentSignPhoto,
                                              MultipartFile marksheet10thCert, MultipartFile marksheet12thCert, MultipartFile graduationMarksheetCert,
                                              MultipartFile nonCreamyLayerCert, MultipartFile incomeCertificateCert, String token);

    StudentDocumentDTO updateStudentDocuments(Long studentId, String role, String email,
                                              MultipartFile studentPhoto, MultipartFile aadharcardPhoto, MultipartFile pancardPhoto,
                                              MultipartFile casteValidationPhoto, MultipartFile casteCertificatePhoto,
                                              MultipartFile leavingCertificatePhoto, MultipartFile domicilePhoto,
                                              MultipartFile birthCertificatePhoto, MultipartFile disabilityCertificate, MultipartFile studentSignPhoto,
                                              MultipartFile marksheet10thCert, MultipartFile marksheet12thCert, MultipartFile graduationMarksheetCert,
                                              MultipartFile nonCreamyLayerCert, MultipartFile incomeCertificateCert);

    Page<StudentResponseDTO> filterStudentsForClassroom(String role, String email, StudentClassRoomFilterDTO filterDTO, Pageable pageable);

    List<StudentResponseDTO> getStudentsByClassRoomId(String role, String email, Long classRoomId);

    void updateStatus(String role, String email, Long studentId, String status, String reason);

    void updateFormStatus(String role, String email, Long studentId);

    StudentDTO getStudentByRegistrationNumber(String role, String email, String registrationNumber);

    void deleteEducationById(String role, String email, Long educationId);

    Map<String, Long> getApplicationCount(String role, String email, String filter, LocalDate customStart, LocalDate customEnd,
                                          String institutionType, Long standardId, Long mediumId,
                                          Long graduationTypeId, Long streamId, String groupName,
                                          Long degreeNameId, String departmentName, String academicYear,
                                          @Nullable String branchCodeFilter);

    LoginResponse studentLogin(LoginRequest request);

    LoginResponse parentLogin(LoginRequest request);

    GenderCountResponse getGenderCount(String role, String email, String institutionType, Long standardId, Long mediumId,
                                       Long graduationTypeId, Long streamId, String groupName,
                                       Long degreeNameId, String departmentName, String academicYear,
                                       @Nullable String branchCodeFilter);

    DataForTcDTO getDataForTc(Long studentId, String role, String email);

    List<ClassRoomStudentCountProjection> getStudentCountByClassRoom(
            String role, String email, String graduationType, String standardName,
            String mediumName, String streamName, String courseType, String degreeName, String departmentName,
            String institutionType, String academicYear, @Nullable String branchCodeFilter);

    StudentPageResponseDTO getStudentsByBranchCode(String role, String email, StudentFilterDTO filter, String timeFrame,
                                                   LocalDate customStart, LocalDate customEnd, Pageable pageable);

    List<StudentCountByCastCategoryDTO> getStudentCountByCastCategory(String role, String email, String institutionType,
                                                                      @Nullable String branchCodeFilter, @Nullable String academicYear);

    List<StudentCountByGenderDTO> getStudentCountByGenderAndAllStandards(String role, String email, String academicYear);

    StudentResponseDTO registerStudent(String role, String email,
                                       StudentRegisterRequest request, MultipartFile oldRegisterPhoto,
                                       MultipartFile entranceMarkSheet, String token);

    List<UpcomingBirthdayProjection> getUpcomingBirthdays(String role, String email);

    List<Map<String, Object>> getStaffInfo(String role, String email, String deptEmail);

    List<ClassRoomStudentCountProjection> getStudentCountByStandard(String role, String email, String academicYear, String mediumName);

}
