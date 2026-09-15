package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StudentSubjectDTO;
import Layer.NewStudentManagement.DTO.StudentTeacherDTO;
import Layer.NewStudentManagement.DTO.TeacherRequestDTO;
import Layer.NewStudentManagement.Entity.*;

import Layer.NewStudentManagement.Repository.*;
import Layer.NewStudentManagement.Security.EmailService;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Security.LoginRequest;
import Layer.NewStudentManagement.Security.LoginResponse;
import Layer.NewStudentManagement.Service.S3Service;
import Layer.NewStudentManagement.Service.TeacherService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeacherServiceImpl implements TeacherService
{
    @Autowired
    private StaffService staffService;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private CourseTypeRepository courseTypeRepository;
    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    S3Service s3Service;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    StreamRepository streamRepository;

    @Autowired
    DegreeNameRepository degreeRepository;



    @Autowired
    ResultRepository resultRepository;

    @Autowired
    private GraduationTypeRepository graduationTypeRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public StudentTeacherDTO createTeacher(String role, String email,MultipartFile profilePhoto, TeacherRequestDTO dto) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to create teacher");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        // Fetch subjects
        List<StudentSubject> subjectEntities = subjectRepository.findAllById(dto.getSubjectIds());
        if (subjectEntities.size() != dto.getSubjectIds().size()) {
            throw new RuntimeException("One or more subject IDs are invalid");
        }


        // Create teacher entity
        StudentTeacher teacher = new StudentTeacher();
        teacher.setTeacherName(dto.getTeacherName());
        teacher.setTeacherEmail(dto.getTeacherEmail());
        teacher.setExperience(dto.getExperience());
        teacher.setEducation(dto.getEducation());
        teacher.setReserch(dto.getReserch());
        teacher.setDob(dto.getDob());
        teacher.setJoiningDate(dto.getJoiningDate());
        teacher.setBranchCode(branchCode);
        teacher.setPassword(passwordEncoder.encode(dto.getPassword()));
        teacher.setRole(role);
        teacher.setCreatedByEmail(email);
        teacher.setSubjects(subjectEntities);
        teacher.setGender(dto.getGender());

        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            String uploadedUrl = s3Service.uploadFile(profilePhoto, branchCode);
            teacher.setProfilePhoto(uploadedUrl);
        }

        // Save and return
        StudentTeacher savedTeacher = teacherRepository.save(teacher);
        return mapToResponseDTO(savedTeacher);
    }


    @Override
    public StudentTeacherDTO getTeacherById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get teacher");
        }
        StudentTeacher teacher = teacherRepository.findByIdAndActiveTrue(id)
                .orElseThrow(()->new RuntimeException("Teacher not found"));
        return mapToResponseDTO(teacher);
    }

    @Override
    public StudentTeacherDTO updateTeacher(Long id, String role, String email, MultipartFile profilePhoto, TeacherRequestDTO teacher) {

        if (!staffService.hasPermission(role, email, "Put")) {
            throw new RuntimeException("You don't have permission to update teacher");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        StudentTeacher existingTeacher = teacherRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            String uploadedUrl = s3Service.uploadFile(profilePhoto, branchCode);
            existingTeacher.setProfilePhoto(uploadedUrl);
        }

        if (teacher.getTeacherName() != null) existingTeacher.setTeacherName(teacher.getTeacherName());
        if (teacher.getTeacherEmail() != null) existingTeacher.setTeacherEmail(teacher.getTeacherEmail());
        if(teacher.getGender() !=null) existingTeacher.setGender(teacher.getGender());
        if (teacher.getPassword() != null) existingTeacher.setPassword(teacher.getPassword());
        if (teacher.getEducation() != null) existingTeacher.setEducation(teacher.getEducation());
        if (teacher.getExperience() != null) existingTeacher.setExperience(teacher.getExperience());
        if (teacher.getReserch() != null) existingTeacher.setReserch(teacher.getReserch());
        if (teacher.getDob() != null) existingTeacher.setDob(teacher.getDob());
        if (teacher.getJoiningDate() != null) existingTeacher.setJoiningDate(teacher.getJoiningDate());

        if (teacher.getSubjectIds() != null && !teacher.getSubjectIds().isEmpty()) {
            List<StudentSubject> subjectEntities = subjectRepository.findAllById(teacher.getSubjectIds());
            for (StudentSubject subject : subjectEntities) {
                if (!subject.getBranchCode().equals(existingTeacher.getBranchCode())) {
                    throw new RuntimeException("Subject " + subject.getSubject() + " doesn't belong to your branch.");
                }
            }
            existingTeacher.setSubjects(subjectEntities);
        }

        StudentTeacher updatedTeacher = teacherRepository.save(existingTeacher);
        return mapToResponseDTO(updatedTeacher);
    }


    @Override
    @Transactional
    public void deleteTeacherById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Delete"))
        {
            throw new RuntimeException("You don't have permission to delete teacher");
        }

        try {
            teacherRepository.deactivateTeacherById(id);
        }
        catch (Exception ex) {
            throw new RuntimeException("Teacher not found");
        }
    }

    @Override
    public List<StudentTeacherDTO> getAllTeacher(String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get teacher");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role,email);
        List<StudentTeacher> teachers = teacherRepository.findAllByBranchCode(branchCode);
        return teachers.stream()
                .map(this::mapToResponseDTO)
                .toList();

    }

    @Override
    public List<StudentTeacherDTO> getTeacherByInstitutionType(String role, String email, String institutionType)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get teacher");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role,email);
        List<StudentTeacher> teachers = teacherRepository.findAllByBranchCode(branchCode);

        return teachers.stream()
                .map(this::mapToResponseDTO)
                .toList();

    }

    @Override
    public LoginResponse login(LoginRequest request) {
        StudentTeacher teacher = teacherRepository.findByTeacherEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), teacher.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(teacher.getTeacherEmail());

        Map<String, Object> teacherData = new HashMap<>();
        teacherData.put("id", teacher.getId());
        teacherData.put("name", teacher.getTeacherName());
        teacherData.put("email", teacher.getTeacherEmail());
        teacherData.put("role", teacher.getRole());
        teacherData.put("branchCode", teacher.getBranchCode());

        return new LoginResponse(token, teacherData);
    }

    @Override
    public String sendOtp(String email) {
        StudentTeacher teacher = teacherRepository.findByTeacherEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit
        teacher.setOtp(otp);
        teacher.setOtpRequestedTime(System.currentTimeMillis());
        teacherRepository.save(teacher);

        emailService.sendOtpEmail(email, otp);
        return "OTP sent to email.";
    }

    @Override
    public String verifyOtp(String email, String otp) {
        StudentTeacher teacher = teacherRepository.findByTeacherEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        if (!otp.equals(teacher.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        long otpAge = System.currentTimeMillis() - teacher.getOtpRequestedTime();
        if (otpAge > 5 * 60 * 1000) { // 5 minutes
            throw new RuntimeException("OTP expired");
        }

        return "OTP verified";
    }

    @Override
    public String resetPassword(String email, String otp, String newPassword) {
        StudentTeacher teacher = teacherRepository.findByTeacherEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        if (!otp.equals(teacher.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        long otpAge = System.currentTimeMillis() - teacher.getOtpRequestedTime();
        if (otpAge > 5 * 60 * 1000) {
            throw new RuntimeException("OTP expired");
        }

        teacher.setPassword(passwordEncoder.encode(newPassword));
        teacher.setOtp(null);
        teacher.setOtpRequestedTime(null);
        teacherRepository.save(teacher);

        return "Password reset successfully";
    }


    @Override
    public List<StudentTeacherDTO> getTeachers(String role, String email, String institutionType, String graduationTypeName, String streamName,
                                               String courseTypeName, String certificationName, String degreeName, String departmentName) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to get teachers");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        List<StudentTeacher> teachers = teacherRepository.findTeachersByFilters(
                branchCode);

        return teachers.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public Map<String, Long> getPassFailCount(String role, String email,Long examId, Long classroomId)
    {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to get Pass/Fail Count");
        }

        List<Object[]> results =
                resultRepository.getPassFailCountByExamAndClassroom(examId, classroomId);

        Map<String, Long> response = new HashMap<>();

        response.put("passCount", 0L);
        response.put("failCount", 0L);

        for (Object[] row : results) {
            String status = (String) row[0];
            Long count = (Long) row[1];

            if ("Pass".equalsIgnoreCase(status)) {
                response.put("passCount", count);
            } else if ("Fail".equalsIgnoreCase(status)) {
                response.put("failCount", count);
            }
        }

        return response;
    }


    private StudentTeacherDTO mapToResponseDTO(StudentTeacher teacher) {
        List<StudentSubjectDTO> subjectDTOs = new ArrayList<>();
        if (teacher.getSubjects() != null) {
            subjectDTOs = teacher.getSubjects().stream()
                    .filter(Objects::nonNull)
                    .map(subject -> {
                        StudentSubjectDTO dto = new StudentSubjectDTO();
                        dto.setId(subject.getId());
                        dto.setSubject(subject.getSubject());
                        return dto;
                    })
                    .collect(Collectors.toList());
        }

        StudentTeacherDTO responseDTO = new StudentTeacherDTO();
        responseDTO.setId(teacher.getId());
        responseDTO.setTeacherName(teacher.getTeacherName());
        responseDTO.setTeacherEmail(teacher.getTeacherEmail());
        responseDTO.setGender(teacher.getGender());
        responseDTO.setProfilePhoto(teacher.getProfilePhoto());
        responseDTO.setEducation(teacher.getEducation());
        responseDTO.setReserch(teacher.getReserch());
        responseDTO.setExperience(teacher.getExperience());
        responseDTO.setBranchCode(teacher.getBranchCode());
        responseDTO.setDob(teacher.getDob());
        responseDTO.setJoiningDate(teacher.getJoiningDate());
        responseDTO.setRole(teacher.getRole());
        responseDTO.setCreatedByEmail(teacher.getCreatedByEmail());
        responseDTO.setSubjects(subjectDTOs);

        return responseDTO;
    }


}
