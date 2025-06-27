package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StudentSubjectDTO;
import Layer.NewStudentManagement.DTO.StudentTeacherDTO;
import Layer.NewStudentManagement.DTO.TeacherRequestDTO;
import Layer.NewStudentManagement.Entity.StudentSubject;
import Layer.NewStudentManagement.Entity.StudentTeacher;
import Layer.NewStudentManagement.Repository.SubjectRepository;
import Layer.NewStudentManagement.Repository.TeacherRepository;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Security.LoginRequest;
import Layer.NewStudentManagement.Security.LoginResponse;
import Layer.NewStudentManagement.Service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TeacherServiceImpl implements TeacherService
{
    @Autowired
    private StaffService staffService;
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public StudentTeacherDTO createTeacher(String role, String email, TeacherRequestDTO dto) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to create teacher");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        List<StudentSubject> subjectEntities = subjectRepository.findAllById(dto.getSubjectIds());

        if (subjectEntities.size() != dto.getSubjectIds().size()) {
            throw new RuntimeException("One or more subject IDs are invalid");
        }

        StudentTeacher teacher = new StudentTeacher();
        teacher.setTeacherName(dto.getTeacherName());
        teacher.setTeacherEmail(dto.getTeacherEmail());
        teacher.setInstitutionType(dto.getInstitutionType());
        teacher.setBranchCode(branchCode);
        teacher.setPassword(passwordEncoder.encode(dto.getPassword()));
        teacher.setRole(role);
        teacher.setCreatedByEmail(email);
        teacher.setSubjects(subjectEntities);

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
        StudentTeacher teacher = teacherRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Teacher not found"));
        return mapToResponseDTO(teacher);
    }

    @Override
    public StudentTeacher updateTeacher(Long id,String role,String email,TeacherRequestDTO teacher)
    {
        if(!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to update teacher");
        }
        StudentTeacher existingTeacher = teacherRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Teacher not found"));

        if (teacher.getTeacherName() != null) {
            existingTeacher.setTeacherName(teacher.getTeacherName());
        }
        if (teacher.getTeacherEmail() != null) {
            existingTeacher.setTeacherEmail(teacher.getTeacherEmail());
        }
        if (teacher.getPassword() != null) {
            existingTeacher.setPassword(teacher.getPassword());
        }
        if (teacher.getSubjectIds() != null && !teacher.getSubjectIds().isEmpty()) {
            List<StudentSubject> subjectEntities = subjectRepository.findAllById(teacher.getSubjectIds());

            for (StudentSubject subject : subjectEntities) {
                if (!subject.getBranchCode().equals(existingTeacher.getBranchCode())) {
                    throw new RuntimeException("Subject " + subject.getSubject() + " doesn't belong to your branch.");
                }
            }
            existingTeacher.setSubjects(subjectEntities);
        }
        return teacherRepository.save(existingTeacher);
    }

    @Override
    public void deleteTeacherById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Delete"))
        {
            throw new RuntimeException("You don't have permission to delete teacher");
        }
        teacherRepository.deleteById(id);

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
        List<StudentTeacher> teachers = teacherRepository.findByInstitutionType(institutionType,branchCode);

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

    private StudentTeacherDTO mapToResponseDTO(StudentTeacher teacher) {
        List<StudentSubjectDTO> subjectDTOs = teacher.getSubjects().stream()
                .map(subject -> {
                    StudentSubjectDTO dto = new StudentSubjectDTO();
                    dto.setId(subject.getId());
                    dto.setSubject(subject.getSubject());
                    return dto;
                })
                .collect(Collectors.toList());

        StudentTeacherDTO responseDTO = new StudentTeacherDTO();
        responseDTO.setId(teacher.getId());
        responseDTO.setTeacherName(teacher.getTeacherName());
        responseDTO.setTeacherEmail(teacher.getTeacherEmail());
        responseDTO.setInstitutionType(teacher.getInstitutionType());
        responseDTO.setBranchCode(teacher.getBranchCode());
        responseDTO.setRole(teacher.getRole());
        responseDTO.setCreatedByEmail(teacher.getCreatedByEmail());
        responseDTO.setSubjects(subjectDTOs);
        return responseDTO;
    }

}
