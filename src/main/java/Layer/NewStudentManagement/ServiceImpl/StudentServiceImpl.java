package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.*;
import Layer.NewStudentManagement.Entity.*;
import Layer.NewStudentManagement.Mapper.StudentMapper;
import Layer.NewStudentManagement.Pagination.StudentSpecification;
import Layer.NewStudentManagement.Repository.*;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Security.LoginRequest;
import Layer.NewStudentManagement.Security.LoginResponse;
import Layer.NewStudentManagement.Service.S3Service;
import Layer.NewStudentManagement.Service.StudentService;
import Layer.NewStudentManagement.Util.BeanCopyUtils;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
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
    @Autowired
    private GraduationTypeRepository graduationTypeRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private DegreeNameRepository degreeNameRepository;
    @Autowired
    private StreamRepository streamRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private TCDataRepository tcDataRepository;
    @Autowired
    private FeesRepository feesRepository;

    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    private void checkPermission(String role, String email, String action) {
        if (!staffService.hasPermission(role, email, action)) {
            throw new RuntimeException("You don't have permission to " + action.toLowerCase() + " student");
        }
    }


    @Override
    public StudentResponseDTO saveStudent(String role, String email, StudentRequest request, String token) {

        String branchCode;
        if ("USER".equalsIgnoreCase(role)) {
            Claims claims = jwtUtil.extractAllClaims(token);
            String encoded = claims.get("branchCode", String.class);

            if (encoded == null || encoded.isEmpty()) {
                throw new RuntimeException("Invalid token: branchCode not found");
            }

            branchCode = new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
        }
        else {
            checkPermission(role, email, "Post");
            branchCode = staffService.fetchBranchCodeByRole(role, email);
        }

        if (studentRepository.existsByEmail(request.getStudent().getEmail())) {
            throw new RuntimeException("Student with this email already exists.");
        }
        StudentEntity student = request.getStudent();
        student.setEnrollmentDate(LocalDate.now());
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        student.setRole(role);
        student.setBranchCode(branchCode);
        student.setCreatedByEmail(email);
        student.setRegistrationNumber(generateRegistrationNumber());
        student.setApplicationNumber(generateApplicationNumber());

        if (request.getGraduationTypeId() != null) {
            StudentGraduationType gradType = graduationTypeRepository.findById(request.getGraduationTypeId())
                    .orElseThrow(() -> new RuntimeException("GraduationType not found with ID: " + request.getGraduationTypeId()));
            student.setGraduationType(gradType);
        }

        if (request.getStreamId() != null) {
            StudentStream stream = streamRepository.findById(request.getStreamId())
                    .orElseThrow(() -> new RuntimeException("Stream not found with ID: " + request.getStreamId()));
            student.setStream(stream);
            student.setStreamName(stream.getStream());
        }

        if ("School".equalsIgnoreCase(student.getInstitutionType())) {

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

            student.setDegreeName(null);
            student.setDepartment(null);
        }

        else if ("College".equalsIgnoreCase(student.getInstitutionType()) &&
                "Jr.College".equalsIgnoreCase(student.getGraduationType().getGraduationType())) {

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

            student.setDegreeName(null);
            student.setDepartment(null);
        }

        else if ("College".equalsIgnoreCase(student.getInstitutionType())) {

            if (request.getDegreeNameId() != null) {
                StudentDegreeName degree = degreeNameRepository.findById(request.getDegreeNameId())
                        .orElseThrow(() -> new RuntimeException("DegreeName not found with ID: " + request.getDegreeNameId()));
                student.setDegreeName(degree);
            }
            if (request.getDepartmentId() != null) {
                StudentDepartment department = departmentRepository.findById(request.getDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Department not found with ID: " + request.getDepartmentId()));
                student.setDepartment(department);
            }
            Long mediumId = request.getMediumId();
            if (mediumId != null) {
                StudentMedium medium = mediumRepository.findById(mediumId)
                        .orElseThrow(() -> new RuntimeException("Medium not found with ID: " + mediumId));
                student.setMedium(medium);
                student.setMediumName(medium.getMediumName());
            }
            student.setGroupName(null);
            student.setStandardName(null);
            student.setStandard(null);
        }

        StudentEntity savedStudent = studentRepository.save(student);

//        // Save Address
//        StudentAddress address = request.getAddress();
//        address.setStudent(savedStudent);
//        addressRepo.save(address);
//
//        // Save Education List
//        List<StudentEducation> educationList = request.getEducationList();
//        for (StudentEducation education : educationList) {
//            education.setStudent(savedStudent);
//            educationRepo.save(education);
//        }
//
//        StudentAdditionalInfo additionalInfo = request.getAdditionalInfo();
//        additionalInfo.setStudent(savedStudent);
//        additionalInfoRepo.save(additionalInfo);
//
//        StudentReligion religion = request.getReligion();
//        religion.setStudent(savedStudent);
//        religionRepo.save(religion);
//
//        StudentSports sports = request.getSports();
//        sports.setStudent(savedStudent);
//        sportsRepo.save(sports);
        try {
            StudentAddress address = request.getAddress();
            if (address != null) {
                address.setStudent(savedStudent);
                addressRepo.save(address);
            }
        } catch (Exception e) {
            logger.error("Address saving failed: {}", e.getMessage());
        }

// Save Education List
        List<StudentEducation> educationList = request.getEducationList();
        if (educationList != null) {
            for (StudentEducation education : educationList) {
                try {
                    education.setStudent(savedStudent);
                    educationRepo.save(education);
                } catch (Exception e) {
                    logger.error("Education saving failed: {}", e.getMessage());
                }
            }
        }

// Save Additional Info
        try {
            StudentAdditionalInfo additionalInfo = request.getAdditionalInfo();
            if (additionalInfo != null) {
                additionalInfo.setStudent(savedStudent);
                additionalInfoRepo.save(additionalInfo);
            }
        } catch (Exception e) {
            logger.error("AdditionalInfo saving failed: {}", e.getMessage());
        }

// Save Religion
        try {
            StudentReligion religion = request.getReligion();
            if (religion != null) {
                religion.setStudent(savedStudent);
                religionRepo.save(religion);
            }
        } catch (Exception e) {
            logger.error("Religion saving failed: {}", e.getMessage());
        }

// Save Sports
        try {
            StudentSports sports = request.getSports();
            if (sports != null) {
                sports.setStudent(savedStudent);
                sportsRepo.save(sports);
            }
        } catch (Exception e) {
            logger.error("Sports saving failed: {}", e.getMessage());
        }

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

        updateStudentFields(existing, request.getStudent());

        if (request.getStudent().getPassword() != null) {
            existing.setPassword(passwordEncoder.encode(request.getStudent().getPassword()));
        }

        StudentEntity savedStudent = studentRepository.save(existing);

        if (request.getStudent().getFullName() != null) {
            List<StudentFees> feesList = feesRepository.findFeesByStudentId(savedStudent.getId());
            if (!feesList.isEmpty()) {
                for (StudentFees fees : feesList) {
                    fees.setStudentName(savedStudent.getFullName()); // update name
                    feesRepository.save(fees);
                }
            }
        }

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
            MultipartFile studentSignPhoto,String token)
    {
        String branchCode;
        if ("USER".equalsIgnoreCase(role)) {
            Claims claims = jwtUtil.extractAllClaims(token);
            String encoded = claims.get("branchCode", String.class);

            if (encoded == null || encoded.isEmpty()) {
                throw new RuntimeException("Invalid token: branchCode not found");
            }

            branchCode = new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
        }
        else {
            checkPermission(role, email, "Post");
            branchCode = staffService.fetchBranchCodeByRole(role, email);
        }

        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        StudentDocument doc = new StudentDocument();

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
    public Page<StudentResponseDTO> filterStudentsForClassroom(String role, String email, StudentClassRoomFilterDTO filterDTO, Pageable pageable) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to filter students.");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        Page<StudentEntity> studentPage;
        String institutionType = filterDTO.getInstitutionType();

        //  Medium
        String medium = filterDTO.getMedium().trim();
        List<Long> mediumIds = mediumRepository.findIdsByName(medium,branchCode);
        if (mediumIds.size() != 1) throw new RuntimeException("Invalid or duplicate medium");
        Long mediumId = mediumIds.get(0);

        if ("school".equalsIgnoreCase(institutionType)) {
            if (filterDTO.getStandard() == null || filterDTO.getAcademicYear() == null) {
                throw new RuntimeException("Standard and academic year are required for school students.");
            }

            String standard = filterDTO.getStandard().trim();
            List<Long> standardIds = standardRepository.findIdsByName(standard,branchCode);
            if (standardIds.size() != 1) throw new RuntimeException("Invalid or duplicate standard");
            Long standardId = standardIds.get(0);

            studentPage = studentRepository.findUnassignedSchoolStudents(
                    standardId,
                    mediumId,
                    filterDTO.getAcademicYear(),
                    pageable
            );
        } else if ("college".equalsIgnoreCase(institutionType)) {
            //  Stream
            String stream = filterDTO.getStreamName().trim();
            List<Long> streamIds = streamRepository.findIdsByNameAndBranchCode(stream, branchCode);
            if (streamIds.size() != 1) throw new RuntimeException("Invalid or duplicate stream");
            Long streamId = streamIds.get(0);

            // Graduation Type
            String graduationType = filterDTO.getGraduationType().trim();
            List<Long> graduationTypeIds = graduationTypeRepository.findIdsByNameAndStreamAndBranchCode(graduationType, streamId, branchCode);
            if (graduationTypeIds.size() != 1) throw new RuntimeException("Invalid or duplicate graduation type");
            Long graduationTypeId = graduationTypeIds.get(0);

            if (filterDTO.getDegreeName() != null && filterDTO.getDepartmentName() != null) {
                // Degree Name
                String degreeName = filterDTO.getDegreeName().trim();
                List<Long> degreeIds = degreeNameRepository.findIdsByNameAndGraduationTypeAndBranchCode(degreeName, graduationTypeId, branchCode);
                if (degreeIds.size() != 1) throw new RuntimeException("Invalid or duplicate degree name");
                Long degreeNameId = degreeIds.get(0);

                //  Department
                String department = filterDTO.getDepartmentName().trim();
                List<Long> departmentIds = departmentRepository.findIdsByNameAndDegreeAndBranchCode(department, degreeNameId, branchCode);
                if (departmentIds.size() != 1) throw new RuntimeException("Invalid or duplicate department name");
                Long departmentId = departmentIds.get(0);

                // Final UG/PG student search
                studentPage = studentRepository.findUnassignedUGPGStudents(
                        mediumId, streamId, degreeNameId, departmentId,
                        filterDTO.getAcademicYear(), pageable);

            } else {
                // Jr. College
                if (filterDTO.getStandard() == null || filterDTO.getGroupName() == null || filterDTO.getAcademicYear() == null) {
                    throw new RuntimeException("Standard, group name, and academic year are required for Jr. College students.");
                }

                String standard = filterDTO.getStandard().trim();
                List<Long> standardIds = standardRepository.findIdsByName(standard,branchCode);
                if (standardIds.size() != 1) throw new RuntimeException("Invalid or duplicate standard");
                Long standardId = standardIds.get(0);

                studentPage = studentRepository.findUnassignedJrCollegeStudents(
                        graduationTypeId,
                        standardId,
                        mediumId,
                        streamId,
                        filterDTO.getGroupName(),
                        filterDTO.getAcademicYear(),
                        pageable
                );
            }

        } else {
            throw new RuntimeException("Invalid institution type: " + institutionType);
        }

        return studentPage.map(this::mapToDTO);
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
        if (incoming.getDiscount() != null) existing.setDiscount(incoming.getDiscount());
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
        if (incoming.getFormStatus() != null) existing.setFormStatus(incoming.getFormStatus());
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
        dto.setFormStatus(student.getFormStatus());
        dto.setReason(student.getReason());
        dto.setDiscount(student.getDiscount());
        dto.setTcGenrated(student.isTcGenrated());
        dto.setOldRegisterPhoto(student.getOldRegisterPhoto());

        if (student.getClassRoom() != null) {
            dto.setClasssRoomId(student.getClassRoom().getId());
        } else {
            dto.setClasssRoomId(null);
        }

        if (student.getSports() != null) {
            dto.setSportYesNo(student.getSports().getSportYesNo());
        } else {
            dto.setSportYesNo(null); // or default value like false
        }
        if (student.getAdditionalInfo() != null) {
            dto.setScholarship(student.getAdditionalInfo().isScholarship());
            dto.setProjectDifferentiated(student.getAdditionalInfo().isProjectDifferentiated());
            dto.setEarthquake(student.getAdditionalInfo().isEarthquake());
            dto.setHandicap(student.getAdditionalInfo().isHandicap());
        } else {
            dto.setScholarship(false);
            dto.setProjectDifferentiated(false);
            dto.setEarthquake(false);
            dto.setHandicap(false);
        }
        dto.setCreatedByEmail(student.getCreatedByEmail());
        dto.setRole(student.getRole());
        dto.setBranchCode(student.getBranchCode());
        if (student.getStandard() != null) {
            dto.setStandardId(student.getStandard().getSid());
            dto.setStandardName(student.getStandard().getStandardName());
        }

        if (student.getMedium() != null) {
            dto.setMediumId(student.getMedium().getMid());
            dto.setMediumName(student.getMedium().getMediumName());
        }
        if (student.getDepartment() != null) {
            dto.setDepartmentId(student.getDepartment().getId());
            dto.setDepartmentName(student.getDepartment().getDepartmentName());
        }
        if (student.getStream() != null) {
            dto.setStreamId(student.getStream().getId());
            dto.setStreamName(student.getStream().getStream());
        }
        if (student.getDegreeName() != null) {
            dto.setDegreeNameId(student.getDegreeName().getId());
            dto.setDegreeName(student.getDegreeName().getDegreeName());

        }
        if (student.getGraduationType() != null) {
            dto.setGraduationTypeId(student.getGraduationType().getId());
            dto.setGraduationType(student.getGraduationType().getGraduationType());
        }
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

                String photoPath = branchCode + "/student-sys/attendance_faces/"
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
    public void updateStatus(String role, String email, Long studentId, String status,String reason)
    {
        checkPermission(role,email,"Put");
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        student.setStatus(status);
        if ("Approved".equalsIgnoreCase(status))
        {
            student.setApprovalDate(LocalDate.now());
            student.setApplicationNumber(generateApplicationNumber());
        }
        else
        {
            student.setReason(reason);
        }

        studentRepository.save(student);
    }

    @Transactional
    public String generateRegistrationNumber() {
        String year = String.valueOf(LocalDate.now().getYear());
        String regNumber;
        int attempt = 0;

        do {
            attempt++;
            Long count = studentRepository.countByRegistrationNumberStartingWith(year);
            String uniquePart = String.format("%08d", count + attempt);
            regNumber = year + uniquePart;
        } while (studentRepository.existsByRegistrationNumber(regNumber));

        return regNumber;
    }


    @Transactional
    public String generateApplicationNumber() {
        String year = String.valueOf(LocalDate.now().getYear());
        String regNumber;
        int attempt = 0;

        do {
            attempt++;
            Long count = studentRepository.countByApplicationNumberStartingWith(year);
            String uniquePart = String.format("%08d", count + attempt);
            regNumber = year + uniquePart;
        } while (studentRepository.existsByApplicationNumber(regNumber));

        return regNumber;
    }


    @Override
    @Transactional
    public void deleteEducationById(String role, String email, Long educationId) {
        checkPermission(role, email, "Delete");

        StudentEducation education = educationRepo.findById(educationId)
                .orElseThrow(() -> new RuntimeException("Education record not found with ID: " + educationId));

        educationRepo.deleteById(educationId);
    }
    @Override
    public void updateFormStatus(String role, String email, Long studentId)
    {
        checkPermission(role,email,"Put");
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        student.setFormStatus("Complete");
        studentRepository.save(student);
    }

    @Override
    public Map<String, Long> getApplicationCount(String role, String email, String filter, LocalDate customStart, LocalDate customEnd,
                                                 String institutionType, Long standardId, Long mediumId,
                                                 Long graduationTypeId, Long streamId, String groupName,
                                                 Long degreeNameId, Long departmentId, String academicYear,
                                                 @Nullable String branchCodeFilter) {

        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You do not have permission to get application count");
        }

        LocalDate today = LocalDate.now();
        LocalDate startDate = null;
        LocalDate endDate = null;

        if (filter != null && !filter.isBlank()) {
            switch (filter.toLowerCase()) {
                case "today" -> {
                    startDate = today;
                    endDate = today;
                }
                case "7days" -> {
                    startDate = today.minusDays(6);
                    endDate = today;
                }
                case "30days" -> {
                    startDate = today.minusDays(29);
                    endDate = today;
                }
                case "365days" -> {
                    startDate = today.minusDays(364);
                    endDate = today;
                }
                case "custom" -> {
                    if (customStart == null || customEnd == null) {
                        throw new IllegalArgumentException("Custom date range must be provided.");
                    }
                    startDate = customStart;
                    endDate = customEnd;
                }
                default -> throw new IllegalArgumentException("Invalid filter: " + filter);
            }
        }
        List<String> branchCodes;

        if ("superadmin".equalsIgnoreCase(role)) {
            branchCodes = staffService.getBranchCodesByInstituteEmail(email);

            if (branchCodes == null || branchCodes.isEmpty()) {
                return Map.of("total", 0L, "approved", 0L, "rejected", 0L, "pending", 0L);
            }

            if (branchCodeFilter != null && !branchCodeFilter.isBlank()) {
                boolean valid = branchCodes.stream()
                        .anyMatch(bc -> bc.equalsIgnoreCase(branchCodeFilter.trim()));

                if (!valid) {
                    return Map.of("total", 0L, "approved", 0L, "rejected", 0L, "pending", 0L);
                }
                branchCodes = List.of(branchCodeFilter.trim());
            }

        } else {
            String userBranchCode = staffService.fetchBranchCodeByRole(role, email);
            if (branchCodeFilter != null && !branchCodeFilter.isBlank()
                    && branchCodeFilter.trim().equalsIgnoreCase(userBranchCode)) {
                branchCodes = List.of(branchCodeFilter.trim());
            } else {
                branchCodes = List.of(userBranchCode);
            }
        }

        long total = 0L, approved = 0L, rejected = 0L, pending = 0L;

        for (String branchCode : branchCodes) {
            Specification<StudentEntity> baseSpec = StudentSpecification.withFilters(
                    institutionType, standardId, mediumId, graduationTypeId,
                    streamId, groupName, degreeNameId, departmentId,
                    startDate, endDate, academicYear);

            Specification<StudentEntity> branchSpec = (root, query, cb) ->
                    cb.equal(root.get("branchCode"), branchCode);

            Specification<StudentEntity> combinedSpec = baseSpec.and(branchSpec);

            long branchTotal = studentRepository.count(combinedSpec);

            Specification<StudentEntity> approvedSpec = combinedSpec.and((root, query, cb) ->
                    cb.equal(root.get("status"), "Approved"));

            Specification<StudentEntity> rejectedSpec = combinedSpec.and((root, query, cb) ->
                    cb.equal(root.get("status"), "Rejected"));

            long branchApproved = studentRepository.count(approvedSpec);
            long branchRejected = studentRepository.count(rejectedSpec);
            long branchPending = branchTotal - branchApproved - branchRejected;

            total += branchTotal;
            approved += branchApproved;
            rejected += branchRejected;
            pending += branchPending;
        }

        Map<String, Long> result = new HashMap<>();
        result.put("total", total);
        result.put("approved", approved);
        result.put("rejected", rejected);
        result.put("pending", pending);

        return result;
    }


    @Override
    public LoginResponse studentLogin(LoginRequest request) {
        StudentEntity student = studentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        student.setUserRole("Student");

        String token = jwtUtil.generateToken(student.getEmail());

        Map<String, Object> studentData = new HashMap<>();
        studentData.put("id", student.getId());
        studentData.put("name", student.getFullName());
        studentData.put("email", student.getEmail());
        studentData.put("role", student.getUserRole());
        studentData.put("branchCode", student.getBranchCode());

        return new LoginResponse(token, studentData);
    }


    @Override
    public GenderCountResponse getGenderCount(String role, String email, String institutionType, Long standardId, Long mediumId,
                                              Long graduationTypeId, Long streamId, String groupName,
                                              Long degreeNameId, Long departmentId, String academicYear,
                                              @Nullable String branchCodeFilter) {

        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You do not have permission to get gender count");
        }

        GenderCountResponse aggregatedResponse = new GenderCountResponse(0L, 0L, 0L);

        if ("superadmin".equalsIgnoreCase(role)) {
            List<String> branchCodes = staffService.getBranchCodesByInstituteEmail(email);
            System.out.println("SuperAdmin - branchCodes for " + email + ": " + branchCodes);

            if (branchCodes == null || branchCodes.isEmpty()) {
                return aggregatedResponse;
            }

            if (branchCodeFilter != null && !branchCodeFilter.isBlank()) {
                // Check if filter is valid branch for this institute
                boolean valid = branchCodes.stream()
                        .anyMatch(bc -> bc.equalsIgnoreCase(branchCodeFilter.trim()));
                if (!valid) {
                    // If invalid filter, return empty counts or throw exception as per your design
                    return aggregatedResponse;
                }
                // Only query for the filtered branch code
                branchCodes = List.of(branchCodeFilter.trim());
            }

            for (String branchCode : branchCodes) {
                GenderCountResponse resp = studentRepository.getGenderCountByFilters(
                        branchCode, institutionType, graduationTypeId, streamId,
                        degreeNameId, departmentId, standardId, mediumId, groupName, academicYear);

                if (resp != null) {
                    aggregatedResponse.setMaleCount(safeSum(aggregatedResponse.getMaleCount(), resp.getMaleCount()));
                    aggregatedResponse.setFemaleCount(safeSum(aggregatedResponse.getFemaleCount(), resp.getFemaleCount()));
                    aggregatedResponse.setOtherCount(safeSum(aggregatedResponse.getOtherCount(), resp.getOtherCount()));
                }
            }

        } else {
            // For other roles, if branchCodeFilter is provided and matches user's branch, use it,
            // else fall back to fetched branch code
            String userBranchCode = staffService.fetchBranchCodeByRole(role, email);

            String branchCodeToUse = userBranchCode;
            if (branchCodeFilter != null && !branchCodeFilter.isBlank()) {
                if (branchCodeFilter.trim().equalsIgnoreCase(userBranchCode)) {
                    branchCodeToUse = branchCodeFilter.trim();
                } else {
                    // Optionally: throw exception or ignore filter if it does not belong to user branch
                    // Here ignoring filter:
                    branchCodeToUse = userBranchCode;
                }
            }

            GenderCountResponse resp = studentRepository.getGenderCountByFilters(
                    branchCodeToUse, institutionType, graduationTypeId, streamId,
                    degreeNameId, departmentId, standardId, mediumId, groupName, academicYear);

            if (resp != null) {
                aggregatedResponse = new GenderCountResponse(
                        safeSum(0L, resp.getMaleCount()),
                        safeSum(0L, resp.getFemaleCount()),
                        safeSum(0L, resp.getOtherCount())
                );
            }
        }

        return aggregatedResponse;
    }

    private Long safeSum(Long a, Long b) {
        return (a == null ? 0L : a) + (b == null ? 0L : b);
    }


    @Override
    public DataForTcDTO getDataForTc(Long studentId, String role, String email) {

        checkPermission(role, email, "Get");

        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        StudentTcData tcData = tcDataRepository.findLatestByStudentId(studentId)
                .orElse(null);

        StudentAddress address = student.getAddress();

        // You had the logic reversed earlier — it should throw if already generated
        if (!student.isTcGenrated()) {
            throw new RuntimeException("TC already generated. Please apply for duplicate TC.");
        }

        DataForTcDTO dto = new DataForTcDTO();
        dto.setFullName(student.getFullName());
        dto.setMotherName(address != null ? address.getMotherName() : null);
        dto.setFathersName(address != null ? address.getFathersName() : null);
        dto.setGender(student.getGender());
        dto.setBloodGroup(student.getBloodGroup());
        dto.setEmail(student.getEmail());
        dto.setDateOfBirth(student.getDateOfBirth());
        dto.setContact(student.getContact());
        dto.setInstitutionType(student.getInstitutionType());
        dto.setStandardId(student.getStandard() != null ? student.getStandard().getSid() : null);
        dto.setStandardName(student.getStandardName());
        dto.setMediumId(student.getMedium() != null ? student.getMedium().getMid() : null);
        dto.setMediumName(student.getMediumName());
        dto.setStreamName(student.getStreamName());
        dto.setStreamId(student.getStream() != null ? student.getStream().getId() : null);
        dto.setGroupName(student.getGroupName());
        dto.setGraduationTypeId(student.getGraduationType() != null ? student.getGraduationType().getId() : null);
        dto.setGraduationType(student.getGraduationType() != null ? student.getGraduationType().getGraduationType() : null);
        dto.setDegreeNameId(student.getDegreeName() != null ? student.getDegreeName().getId() : null);
        dto.setDegreeName(student.getDegreeName() != null ? student.getDegreeName().getDegreeName() : null);
        dto.setDepartmentId(student.getDepartment() != null ? student.getDepartment().getId() : null);
        dto.setDepartmentName(student.getDepartment() != null ? student.getDepartment().getDepartmentName() : null);
        dto.setAcademicYear(student.getAcademicYear());
        dto.setRegistrationNumber(student.getRegistrationNumber());
        dto.setRollNo(student.getRollNo());
        dto.setPermanentAddress(address != null ? address.getPermanentAddress() : null);

        dto.setTcGenrated(student.isTcGenrated());
        dto.setDuplicateTc(tcData != null && tcData.isDuplicateTc());


        return dto;
    }


    @Override
    public List<ClassRoomStudentCountProjection> getStudentCountByClassRoom(
            String role, String email, String graduationType, String standardName,
            String mediumName, String streamName, String degreeName, String departmentName,
            String institutionType, String academicYear) {

        checkPermission(role, email, "Get");

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        if (branchCode == null || branchCode.trim().isEmpty()) {
            throw new RuntimeException("Branch code not found for the given role and email");
        }

        return studentRepository.getStudentCountByClassRoomWithFilters(
                branchCode,
                graduationType != null && !graduationType.trim().isEmpty() ? graduationType : null,
                standardName != null && !standardName.trim().isEmpty() ? standardName : null,
                mediumName != null && !mediumName.trim().isEmpty() ? mediumName : null,
                streamName != null && !streamName.trim().isEmpty() ? streamName : null,
                degreeName != null && !degreeName.trim().isEmpty() ? degreeName : null,
                departmentName != null && !departmentName.trim().isEmpty() ? departmentName : null,
                institutionType != null && !institutionType.trim().isEmpty() ? institutionType : null,
                academicYear != null && !academicYear.trim().isEmpty() ? academicYear : null
        );
    }


    @Override
    public Page<StudentResponseDTO> getStudentsByBranchCode(String role, String email, String status, String fullName,
                                                            String institutionType, Pageable pageable)
    {

        checkPermission(role, email, "Get");

        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        Page<StudentEntity> students = studentRepository.findAll(
                StudentSpecification.filter(branchCode, status, fullName, institutionType),
                pageable
        );

        return students.map(this::mapToDTO);
    }


    @Override
    public List<StudentCountByCastCategoryDTO> getStudentCountByCastCategory(String role, String email, String institutionType)
    {
        checkPermission(role, email, "Get");

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        return religionRepo.getStudentCountByCastCategory(branchCode, institutionType);
    }


    @Override
    public List<StudentCountByGenderDTO> getStudentCountByGenderAndAllStandards(String role, String email) {
        checkPermission(role, email, "Get");

        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        List<Object[]> rawData = studentRepository.getRawStudentCountByGenderAndStandard(branchCode);

        Map<String, StudentCountByGenderDTO> resultMap = new HashMap<>();

        for (Object[] row : rawData) {
            String standardName = (String) row[0];
            String gender = (String) row[1];
            Long count = (Long) row[2];

            StudentCountByGenderDTO dto = resultMap.getOrDefault(
                    standardName,
                    new StudentCountByGenderDTO(standardName, 0L, 0L)
            );

            if ("Male".equalsIgnoreCase(gender)) {
                dto.setMale(count);
            } else if ("Female".equalsIgnoreCase(gender)) {
                dto.setFemale(count);
            }

            resultMap.put(standardName, dto);
        }

        return new ArrayList<>(resultMap.values());
    }


    @Override
    public StudentResponseDTO registerStudent(String role, String email,
                                              StudentRegisterRequest request,
                                              MultipartFile oldRegisterPhoto,
                                              String token) {
        String branchCode;
        if ("USER".equalsIgnoreCase(role)) {
            Claims claims = jwtUtil.extractAllClaims(token);
            String encoded = claims.get("branchCode", String.class);
            if (encoded == null || encoded.isEmpty()) {
                throw new RuntimeException("Invalid token: branchCode not found");
            }
            branchCode = new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
        } else {
            checkPermission(role, email, "Post");
            branchCode = staffService.fetchBranchCodeByRole(role, email);
        }

        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Student with this email already exists.");
        }

        StudentEntity student = new StudentEntity();
        student.setTitle(request.getTitle());
        student.setFullName(request.getFullName());
        student.setGender(request.getGender());
        student.setContact(request.getContact());
        student.setEmail(request.getEmail());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setStatus(request.getStatus());
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setInstitutionType(request.getInstitutionType());
        student.setAcademicYear(request.getAcademicYear());
        student.setDiscount(request.getDiscount());
        student.setRole(role);
        student.setBranchCode(branchCode);
        student.setCreatedByEmail(email);
        student.setEnrollmentDate(LocalDate.now());
        student.setRegistrationNumber(generateRegistrationNumber());
        student.setFormStatus(request.getFormStatus());

        // ---- Upload Photo ----
        if (oldRegisterPhoto != null && !oldRegisterPhoto.isEmpty()) {
            String fileUrl = s3Service.uploadFile(oldRegisterPhoto, branchCode);
            student.setOldRegisterPhoto(fileUrl);
        }

        if ("School".equalsIgnoreCase(student.getInstitutionType())) {
            if (request.getStandardId() != null) {
                StudentStandard standard = standardRepository.findById(request.getStandardId())
                        .orElseThrow(() -> new RuntimeException("Standard not found"));
                student.setStandard(standard);
                student.setStandardName(standard.getStandardName());
            }
            if (request.getMediumId() != null) {
                StudentMedium medium = mediumRepository.findById(request.getMediumId())
                        .orElseThrow(() -> new RuntimeException("Medium not found"));
                student.setMedium(medium);
                student.setMediumName(medium.getMediumName());
            }
            student.setDegreeName(null);
            student.setDepartment(null);
        } else if ("College".equalsIgnoreCase(student.getInstitutionType()) &&
                request.getGraduationTypeId() != null &&
                "Jr.College".equalsIgnoreCase(
                        graduationTypeRepository.findById(request.getGraduationTypeId())
                                .orElseThrow(() -> new RuntimeException("GraduationType not found"))
                                .getGraduationType())) {

            if (request.getStandardId() != null) {
                StudentStandard standard = standardRepository.findById(request.getStandardId())
                        .orElseThrow(() -> new RuntimeException("Standard not found"));
                student.setStandard(standard);
                student.setStandardName(standard.getStandardName());
            }
            if (request.getMediumId() != null) {
                StudentMedium medium = mediumRepository.findById(request.getMediumId())
                        .orElseThrow(() -> new RuntimeException("Medium not found"));
                student.setMedium(medium);
                student.setMediumName(medium.getMediumName());
            }
            student.setDegreeName(null);
            student.setDepartment(null);
        } else if ("College".equalsIgnoreCase(student.getInstitutionType())) {
            if (request.getDegreeNameId() != null) {
                StudentDegreeName degree = degreeNameRepository.findById(request.getDegreeNameId())
                        .orElseThrow(() -> new RuntimeException("DegreeName not found"));
                student.setDegreeName(degree);
            }
            if (request.getDepartmentId() != null) {
                StudentDepartment dept = departmentRepository.findById(request.getDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Department not found"));
                student.setDepartment(dept);
            }
            if (request.getMediumId() != null) {
                StudentMedium medium = mediumRepository.findById(request.getMediumId())
                        .orElseThrow(() -> new RuntimeException("Medium not found"));
                student.setMedium(medium);
                student.setMediumName(medium.getMediumName());
            }
            student.setStandard(null);
            student.setStandardName(null);
            student.setGroupName(null);
        }

        StudentEntity savedStudent = studentRepository.save(student);
        return mapToDTO(savedStudent);
    }



}
