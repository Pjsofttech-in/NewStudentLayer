package Layer.NewStudentManagement.ServiceImpl;

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
    public StudentTeacher createTeacher(String role, String email, TeacherRequestDTO dto) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to create teacher");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        List<StudentSubject> subjectEntities = subjectRepository.findAllById(dto.getSubjectIds());
        List<String> subjectNames = subjectEntities.stream()
                .map(StudentSubject::getSubject)
                .collect(Collectors.toList());

        StudentTeacher teacher = new StudentTeacher();
        teacher.setTeacherName(dto.getTeacherName());
        teacher.setTeacherEmail(dto.getTeacherEmail());
        teacher.setBranchCode(branchCode);
        teacher.setPassword(passwordEncoder.encode(dto.getPassword())); // use password from DTO
        teacher.setRole(role);
        teacher.setCreatedByEmail(email);
        teacher.setSubjectsFromNames(subjectNames);
        return teacherRepository.save(teacher);
    }

    @Override
    public StudentTeacher getTeacherById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get teacher");
        }
        StudentTeacher teacher = teacherRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Teacher not found"));
        return teacher;
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
        if (teacher.getSubjectIds() != null) {
            List<StudentSubject> subjectEntities = subjectRepository.findAllById(teacher.getSubjectIds());
            List<String> subjectNames = subjectEntities.stream()
                    .map(StudentSubject::getSubject)
                    .collect(Collectors.toList());
            existingTeacher.setSubjectsFromNames(subjectNames); // update subjects
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
    public List<StudentTeacher> getAllTeacher(String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get teacher");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role,email);
        return teacherRepository.findAllByBranchCode(branchCode);

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

}
