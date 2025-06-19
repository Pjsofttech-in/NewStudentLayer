package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.*;
import Layer.NewStudentManagement.Entity.*;
import Layer.NewStudentManagement.Mapper.StudentMapper;
import Layer.NewStudentManagement.Pagination.StudentSpecification;
import Layer.NewStudentManagement.Repository.*;
import Layer.NewStudentManagement.Service.S3Service;
import Layer.NewStudentManagement.Service.StudentService;
import Layer.NewStudentManagement.Util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private AddressRepository addressRepo;
    @Autowired
    private EducationRepository educationRepo;
    @Autowired
    private AdditionalInfoRepository additionalInfoRepo;
    @Autowired
    private ReligionRepository religionRepo;
    @Autowired
    private SportsRepository sportsRepo;
    @Autowired
    private StaffService staffService;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private MediumRepository mediumRepository;
    @Autowired
    private StandardRepository standardRepository;



    private void checkPermission(String role, String email, String action) {
        if (!staffService.hasPermission(role, email, action)) {
            throw new RuntimeException("You don't have permission to " + action.toLowerCase() + " student");
        }
    }


    @Override
    public StudentResponseDTO  saveStudent(String role, String email, StudentRequest request) {
        checkPermission(role, email, "Post");
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        StudentEntity student = request.getStudent();

        Long standardId = request.getStandardId();
        if (standardId != null) {
            StudentStandard standard = standardRepository.findById(standardId)
                    .orElseThrow(() -> new RuntimeException("Standard not found with ID: " + standardId));
            student.setStandard(standard);
            student.setStandardName(standard.getStandardName());
        }
        Long mediumId = request.getMediumId();
        if (mediumId != null) {
            StudentMedium medium = mediumRepository.findById(mediumId)
                    .orElseThrow(() -> new RuntimeException("Medium not found with ID: " + mediumId));
            student.setMedium(medium);
            student.setMediumName(medium.getMediumName());
        }

        student.setEnrollmentDate(LocalDate.now());
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        student.setRole(role);
        student.setBranchCode(branchCode);
        student.setCreatedByEmail(email);
        student.setRegistrationNumber(generateRegistrationNumber());

        StudentEntity savedStudent = studentRepository.save(student);

        StudentAddress address = request.getAddress();
        address.setStudent(savedStudent);
        addressRepo.save(address);

        List<StudentEducation> educationList = request.getEducationList();
        for (StudentEducation education : educationList) {
            education.setStudent(savedStudent);
            educationRepo.save(education);
        }

        StudentAdditionalInfo additionalInfo = request.getAdditionalInfo();
        additionalInfo.setStudent(savedStudent);
        additionalInfoRepo.save(additionalInfo);

        StudentReligion religion = request.getReligion();
        religion.setStudent(savedStudent);
        religionRepo.save(religion);

        StudentSports sports = request.getSports();
        sports.setStudent(savedStudent);
        sportsRepo.save(sports);


        return mapToDTO(savedStudent);

    }

    @Override
    public StudentDTO getStudentById(Long id, String role, String email) {
        checkPermission(role, email, "Get");
        StudentEntity student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));

        return studentMapper.toStudentDTO(student);

    }

    @Override
    public StudentResponseDTO updateStudent(Long studentId, String role, String email, StudentRequest request) {
        checkPermission(role, email, "Put");

        StudentEntity existing = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        BeanCopyUtils.copyNonNullProperties(request.getStudent(), existing);

        if (request.getStudent().getPassword() != null) {
            existing.setPassword(passwordEncoder.encode(request.getStudent().getPassword()));
        }

        StudentEntity savedStudent = studentRepository.save(existing);

        Optional.ofNullable(request.getAddress()).ifPresent(updatedAddr -> {
            StudentAddress existingAddress = addressRepo.findByStudentId(studentId)
                    .orElse(new StudentAddress());
            BeanCopyUtils.copyNonNullProperties(updatedAddr, existingAddress);
            existingAddress.setStudent(savedStudent);
            addressRepo.save(existingAddress);
        });

        Optional.ofNullable(request.getEducationList()).ifPresent(list -> {
            for (StudentEducation updatedEdu : list) {
                if (updatedEdu.getId() != null) {
                    StudentEducation existingEdu = educationRepo.findById(updatedEdu.getId())
                            .orElse(new StudentEducation());
                    BeanCopyUtils.copyNonNullProperties(updatedEdu, existingEdu);
                    existingEdu.setStudent(savedStudent);
                    educationRepo.save(existingEdu);
                } else {
                    updatedEdu.setStudent(savedStudent);
                    educationRepo.save(updatedEdu);
                }
            }
        });

        Optional.ofNullable(request.getAdditionalInfo()).ifPresent(updated -> {
            StudentAdditionalInfo existingAdd = additionalInfoRepo.findByStudentId(studentId)
                    .orElse(new StudentAdditionalInfo());
            BeanCopyUtils.copyNonNullProperties(updated, existingAdd);
            existingAdd.setStudent(savedStudent);
            additionalInfoRepo.save(existingAdd);
        });

        Optional.ofNullable(request.getReligion()).ifPresent(updated -> {
            StudentReligion existingRel = religionRepo.findByStudentId(studentId)
                    .orElse(new StudentReligion());
            BeanCopyUtils.copyNonNullProperties(updated, existingRel);
            existingRel.setStudent(savedStudent);
            religionRepo.save(existingRel);
        });

        Optional.ofNullable(request.getSports()).ifPresent(updated -> {
            StudentSports existingSports = sportsRepo.findByStudentId(studentId)
                    .orElse(new StudentSports());
            BeanCopyUtils.copyNonNullProperties(updated, existingSports);
            existingSports.setStudent(savedStudent);
            sportsRepo.save(existingSports);
        });

        return mapToDTO(savedStudent);
    }


    @Override
    public void deleteStudentById(Long id, String role, String email) {
        checkPermission(role, email, "Delete");
        StudentEntity student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));
        studentRepository.delete(student);
    }

    @Override
    public Page<StudentResponseDTO> getAllStudent(String role, String email, StudentFilterDTO filter,
                                                  String timeFrame, LocalDate customStart, LocalDate customEnd,
                                                  Pageable pageable) {
        checkPermission(role, email, "Post");
        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        Specification<StudentEntity> spec = StudentSpecification.build(filter, branchCode, timeFrame, customStart, customEnd);

        return studentRepository.findAll(spec, pageable).map(this::mapToDTO);
    }


    public StudentDocumentDTO uploadStudentDocuments(
            Long studentId, String role, String email,
            MultipartFile studentPhoto, MultipartFile aadharcardPhoto, MultipartFile pancardPhoto,
            MultipartFile casteValidationPhoto, MultipartFile casteCertificatePhoto,
            MultipartFile leavingCertificatePhoto, MultipartFile domicilePhoto,
            MultipartFile birthCertificatePhoto, MultipartFile disabilityCertificate,
            MultipartFile studentSignPhoto) {
        checkPermission(role, email, "Post");
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        StudentDocument doc = new StudentDocument();
        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        if (studentPhoto != null)
            doc.setStudentPhoto(s3Service.uploadFile(studentPhoto, branchCode));
        if (aadharcardPhoto != null)
            doc.setAadharcardPhoto(s3Service.uploadFile(aadharcardPhoto, branchCode));
        if (pancardPhoto != null)
            doc.setPancardPhoto(s3Service.uploadFile(pancardPhoto, branchCode));
        if (casteValidationPhoto != null)
            doc.setCasteValidationPhoto(s3Service.uploadFile(casteValidationPhoto, branchCode));
        if (casteCertificatePhoto != null)
            doc.setCasteCertificatePhoto(s3Service.uploadFile(casteCertificatePhoto, branchCode));
        if (leavingCertificatePhoto != null)
            doc.setLeavingCertificatePhoto(s3Service.uploadFile(leavingCertificatePhoto, branchCode));
        if (domicilePhoto != null)
            doc.setDomicilePhoto(s3Service.uploadFile(domicilePhoto, branchCode));
        if (birthCertificatePhoto != null)
            doc.setBirthCertificatePhoto(s3Service.uploadFile(birthCertificatePhoto, branchCode));
        if (disabilityCertificate != null)
            doc.setDisabilityCertificate(s3Service.uploadFile(disabilityCertificate, branchCode));
        if (studentSignPhoto != null)
            doc.setStudentSignPhoto(s3Service.uploadFile(studentSignPhoto, branchCode));

        doc.setStudent(student);

        StudentDocument saved = documentRepository.save(doc);

        StudentDocumentDTO dto = new StudentDocumentDTO();
        dto.setId(saved.getId());
        dto.setStudentPhoto(saved.getStudentPhoto());
        dto.setAadharcardPhoto(saved.getAadharcardPhoto());
        dto.setPancardPhoto(saved.getPancardPhoto());
        dto.setCasteValidationPhoto(saved.getCasteValidationPhoto());
        dto.setCasteCertificatePhoto(saved.getCasteCertificatePhoto());
        dto.setLeavingCertificatePhoto(saved.getLeavingCertificatePhoto());
        dto.setDomicilePhoto(saved.getDomicilePhoto());
        dto.setBirthCertificatePhoto(saved.getBirthCertificatePhoto());
        dto.setDisabilityCertificate(saved.getDisabilityCertificate());
        dto.setStudentSignPhoto(saved.getStudentSignPhoto());
        dto.setStudentId(saved.getStudent().getId());

        return dto;
    }

    @Override
    public List<StudentResponseDTO> getStudentByMediumDivisionStandard(String role, String email, String medium, String standard, String year, String status) {
        checkPermission(role, email, "Get");
        List<StudentEntity> students = studentRepository.findByMediumAndStandard(medium, standard, year, status);

        return students.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StudentDTO getStudentByRegistrationNumber(String role, String email, String registrationNumber) {
        checkPermission(role, email, "Get");

        StudentEntity student = studentRepository.findByRegistrationNumberWithAllData(registrationNumber)
                .orElseThrow(() -> new RuntimeException("Student not found with registration number: " + registrationNumber));

        return studentMapper.toStudentDTO(student); // or manually map to StudentDTO
    }



    private void updateStudentFields(StudentEntity existing, StudentEntity incoming) {
        if (incoming.getTitle() != null) existing.setTitle(incoming.getTitle());
        if (incoming.getFullName() != null) existing.setFullName(incoming.getFullName());
        if (incoming.getGender() != null) existing.setGender(incoming.getGender());
        if (incoming.getBloodGroup() != null) existing.setBloodGroup(incoming.getBloodGroup());
        if (incoming.getMotherTongue() != null) existing.setMotherTongue(incoming.getMotherTongue());
        if (incoming.getMaritalStatus() != null) existing.setMaritalStatus(incoming.getMaritalStatus());
        if (incoming.getContact() != null && !"null".equals(incoming.getContact()))
            existing.setContact(incoming.getContact());
        if (incoming.getAge() != null) existing.setAge(incoming.getAge());
        if (incoming.getEmail() != null) existing.setEmail(incoming.getEmail());
        if (incoming.getDateOfBirth() != null) existing.setDateOfBirth(incoming.getDateOfBirth());
        if (incoming.getBirthPlace() != null) existing.setBirthPlace(incoming.getBirthPlace());
        if (incoming.getBirthCountry() != null) existing.setBirthCountry(incoming.getBirthCountry());
        if (incoming.getPancardNumber() != null) existing.setPancardNumber(incoming.getPancardNumber());
        if (incoming.getAadharNumber() != null) existing.setAadharNumber(incoming.getAadharNumber());
        if (incoming.getRollNo() != null) existing.setRollNo(incoming.getRollNo());
        if (incoming.getStandard() != null) existing.setStandard(incoming.getStandard());
        if (incoming.getAcademicYear() != null) existing.setAcademicYear(incoming.getAcademicYear());
        if (incoming.getUdiseNo() != null) existing.setUdiseNo(incoming.getUdiseNo());
        if (incoming.getApaarId() != null) existing.setApaarId(incoming.getApaarId());
        if (incoming.getMediumName() != null) existing.setMediumName(incoming.getMediumName());
        if (incoming.getEnrollmentDate() != null) existing.setEnrollmentDate(incoming.getEnrollmentDate());
        if (incoming.getApprovalDate() != null) existing.setApprovalDate(incoming.getApprovalDate());
        if (incoming.getStatus() != null) existing.setStatus(incoming.getStatus());
        if (incoming.getApplyFor() != null) existing.setApplyFor(incoming.getApplyFor());
        if (incoming.getStreamName() != null) existing.setStreamName(incoming.getStreamName());
        if (incoming.getGroupName() != null) existing.setGroupName(incoming.getGroupName());
        if (incoming.getSemister() != null) existing.setSemister(incoming.getSemister());
        if (incoming.getInstitutionType() != null) existing.setInstitutionType(incoming.getInstitutionType());
    }


    public StudentResponseDTO mapToDTO(StudentEntity student) {
        StudentResponseDTO dto = new StudentResponseDTO();

        dto.setId(student.getId());
        dto.setTitle(student.getTitle());
        dto.setFullName(student.getFullName());
        dto.setGender(student.getGender());
        dto.setBloodGroup(student.getBloodGroup());
        dto.setMotherTongue(student.getMotherTongue());
        dto.setMaritalStatus(student.getMaritalStatus());

        dto.setContact(student.getContact() != null ? String.valueOf(student.getContact()) : null); // Safely convert
        dto.setAge(student.getAge());
        dto.setEmail(student.getEmail());
        dto.setDateOfBirth(student.getDateOfBirth());
        dto.setBirthPlace(student.getBirthPlace());
        dto.setBirthCountry(student.getBirthCountry());
        dto.setPancardNumber(student.getPancardNumber());
        dto.setAadharNumber(student.getAadharNumber());
        dto.setRollNo(student.getRollNo());
        dto.setStandardName(student.getStandardName());
        dto.setAcademicYear(student.getAcademicYear());
        dto.setUdiseNo(student.getUdiseNo());
        dto.setApaarId(student.getApaarId());
        dto.setMediumName(student.getMediumName());
        dto.setEnrollmentDate(student.getEnrollmentDate());
        dto.setApprovalDate(student.getApprovalDate());
        dto.setStatus(student.getStatus());
        dto.setApplyFor(student.getApplyFor());
        dto.setStreamName(student.getStreamName());
        dto.setGroupName(student.getGroupName());
        dto.setSemister(student.getSemister());
        dto.setInstitutionType(student.getInstitutionType());
        dto.setRegistrationNumber(student.getRegistrationNumber());

        // Safely get IDs from linked entities
        if (student.getStandard() != null) {
            dto.setStandardId(student.getStandard().getSid());
            dto.setStandardName(student.getStandard().getStandardName());
        }

        if (student.getMedium() != null) {
            dto.setMediumId(student.getMedium().getMid());
            dto.setMediumName(student.getMedium().getMediumName());
        }

        dto.setCreatedByEmail(student.getCreatedByEmail());
        dto.setRole(student.getRole());
        dto.setBranchCode(student.getBranchCode());
        if (student.getAdditionalInfo() != null) {
            dto.setEarthquake(student.getAdditionalInfo().isEarthquake());
            dto.setHandicap(student.getAdditionalInfo().isHandicap());
        }

        if (student.getReligion() != null) {
            dto.setCastCategory(student.getReligion().getCastCategory());
            dto.setMinority(student.getReligion().isMinority());
        }

        return dto;
    }

    @Override
    public StudentDocumentDTO updateStudentDocuments(Long studentId, String role, String email,
                                                     MultipartFile studentPhoto,
                                                     MultipartFile aadharcardPhoto,
                                                     MultipartFile pancardPhoto,
                                                     MultipartFile casteValidationPhoto,
                                                     MultipartFile casteCertificatePhoto,
                                                     MultipartFile leavingCertificatePhoto,
                                                     MultipartFile domicilePhoto,
                                                     MultipartFile birthCertificatePhoto,
                                                     MultipartFile disabilityCertificate,
                                                     MultipartFile studentSignPhoto) {
        checkPermission(role, email, "Put");

        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        StudentDocument doc = documentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("StudentDocument not found"));

        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        // Student Photo Upload Logic
        if (studentPhoto != null && !studentPhoto.isEmpty()) {
            s3Service.deleteFileFromUrl(doc.getStudentPhoto());
            String uploadedUrl;

            if (student.getClassRoom() != null && student.getRollNo() != null) {
                String originalFilename = studentPhoto.getOriginalFilename();
                String extension = originalFilename != null && originalFilename.contains(".")
                        ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                        : "";

                String photoPath = branchCode + "/student_sys/attendance_faces/"
                        + student.getClassRoom().getId() + "/" + student.getRollNo() + extension;

                uploadedUrl = s3Service.uploadFileToExactPath(studentPhoto, photoPath);
            } else {
                uploadedUrl = s3Service.uploadFile(studentPhoto, branchCode);
            }

            doc.setStudentPhoto(uploadedUrl);
        }

        if (aadharcardPhoto != null && !aadharcardPhoto.isEmpty()) {
            s3Service.deleteFileFromUrl(doc.getAadharcardPhoto());
            doc.setAadharcardPhoto(s3Service.uploadFile(aadharcardPhoto, branchCode));
        }

        if (pancardPhoto != null && !pancardPhoto.isEmpty()) {
            s3Service.deleteFileFromUrl(doc.getPancardPhoto());
            doc.setPancardPhoto(s3Service.uploadFile(pancardPhoto, branchCode));
        }

        if (casteValidationPhoto != null && !casteValidationPhoto.isEmpty()) {
            s3Service.deleteFileFromUrl(doc.getCasteValidationPhoto());
            doc.setCasteValidationPhoto(s3Service.uploadFile(casteValidationPhoto, branchCode));
        }

        if (casteCertificatePhoto != null && !casteCertificatePhoto.isEmpty()) {
            s3Service.deleteFileFromUrl(doc.getCasteCertificatePhoto());
            doc.setCasteCertificatePhoto(s3Service.uploadFile(casteCertificatePhoto, branchCode));
        }

        if (leavingCertificatePhoto != null && !leavingCertificatePhoto.isEmpty()) {
            s3Service.deleteFileFromUrl(doc.getLeavingCertificatePhoto());
            doc.setLeavingCertificatePhoto(s3Service.uploadFile(leavingCertificatePhoto, branchCode));
        }

        if (domicilePhoto != null && !domicilePhoto.isEmpty()) {
            s3Service.deleteFileFromUrl(doc.getDomicilePhoto());
            doc.setDomicilePhoto(s3Service.uploadFile(domicilePhoto, branchCode));
        }

        if (birthCertificatePhoto != null && !birthCertificatePhoto.isEmpty()) {
            s3Service.deleteFileFromUrl(doc.getBirthCertificatePhoto());
            doc.setBirthCertificatePhoto(s3Service.uploadFile(birthCertificatePhoto, branchCode));
        }

        if (disabilityCertificate != null && !disabilityCertificate.isEmpty()) {
            s3Service.deleteFileFromUrl(doc.getDisabilityCertificate());
            doc.setDisabilityCertificate(s3Service.uploadFile(disabilityCertificate, branchCode));
        }

        if (studentSignPhoto != null && !studentSignPhoto.isEmpty()) {
            s3Service.deleteFileFromUrl(doc.getStudentSignPhoto());
            doc.setStudentSignPhoto(s3Service.uploadFile(studentSignPhoto, branchCode));
        }

        StudentDocument saved = documentRepository.save(doc);

        StudentDocumentDTO dto = new StudentDocumentDTO();
        dto.setId(saved.getId());
        dto.setStudentId(student.getId());
        dto.setStudentPhoto(saved.getStudentPhoto());
        dto.setAadharcardPhoto(saved.getAadharcardPhoto());
        dto.setPancardPhoto(saved.getPancardPhoto());
        dto.setCasteValidationPhoto(saved.getCasteValidationPhoto());
        dto.setCasteCertificatePhoto(saved.getCasteCertificatePhoto());
        dto.setLeavingCertificatePhoto(saved.getLeavingCertificatePhoto());
        dto.setDomicilePhoto(saved.getDomicilePhoto());
        dto.setBirthCertificatePhoto(saved.getBirthCertificatePhoto());
        dto.setDisabilityCertificate(saved.getDisabilityCertificate());
        dto.setStudentSignPhoto(saved.getStudentSignPhoto());

        return dto;
    }


    @Override
    public List<StudentResponseDTO> getStudentsByClassRoomId(String role, String email,Long classRoomId)
    {
        checkPermission(role,email,"Get");
        List<StudentEntity> students = studentRepository.findByClassRoomId(classRoomId);
        return students.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public void updateStatus(String role, String email, Long studentId, String status)
    {
        checkPermission(role,email,"Put");
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        student.setStatus(status);
        if ("Approved".equalsIgnoreCase(status)) {
            student.setApprovalDate(LocalDate.now());
        }

        studentRepository.save(student);
    }

    private String generateRegistrationNumber() {
        String year = String.valueOf(LocalDate.now().getYear());
        Long count = studentRepository.countByRegistrationNumberStartingWith(year);
        String uniquePart = String.format("%08d", count + 1);

        return year + uniquePart;  // e.g., "202500000001"
    }



}
