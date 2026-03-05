package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StudentSubjectDTO;
import Layer.NewStudentManagement.Entity.*;
import Layer.NewStudentManagement.Enum.Role;
import Layer.NewStudentManagement.Repository.*;
import Layer.NewStudentManagement.Service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectServiceImpl implements SubjectService
{

    @Autowired
    private StaffService staffService;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private StreamRepository streamRepository;

    @Autowired
    private GraduationTypeRepository graduationTypeRepository;

    @Autowired
    private DegreeNameRepository degreeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public StudentSubjectDTO saveSubject(StudentSubjectDTO dto, String role, String email) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to create subject");
        }

        StudentSubject subject = new StudentSubject();
        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        subject.setBranchCode(branchCode);
        subject.setRole(Role.valueOf(role));
        subject.setCreatedByEmail(email);
        subject.setInstitutionType(dto.getInstitutionType());
        subject.setSubject(dto.getSubject());

        String institutionType = dto.getInstitutionType();
        String graduationTypeName = null;

        // Validate and Map based on Institution Type
        if ("School".equalsIgnoreCase(institutionType)) {
            // Set all academic fields null
            subject.setGraduationType(null);
            subject.setStream(null);
            subject.setDegree(null);
            subject.setDepartment(null);
        }
        else if ("College".equalsIgnoreCase(institutionType)) {
            // Graduation Type must be present
            if (dto.getGraduationTypeId() == null) {
                throw new RuntimeException("Graduation Type is required for College");
            }

            StudentGraduationType graduationType = graduationTypeRepository.findById(dto.getGraduationTypeId())
                    .orElseThrow(() -> new RuntimeException("Graduation Type not found"));
            graduationTypeName = graduationType.getGraduationType();
            subject.setGraduationType(graduationType);
            subject.setGraduationTypeName(graduationTypeName);

            if ("Jr.College".equalsIgnoreCase(graduationTypeName)) {
                // Only Stream is required
                if (dto.getStreamId() == null) {
                    throw new RuntimeException("Stream is required for Jr.College");
                }
                StudentStream stream = streamRepository.findById(dto.getStreamId())
                        .orElseThrow(() -> new RuntimeException("Stream not found"));
                subject.setStream(stream);
                subject.setStreamName(stream.getStream());

                subject.setDegree(null);
                subject.setDepartment(null);
            }
            else if ("UG".equalsIgnoreCase(graduationTypeName) || "PG".equalsIgnoreCase(graduationTypeName)) {
                // All fields are required
                if (dto.getStreamId() == null || dto.getDegreeId() == null || dto.getDepartmentId() == null) {
                    throw new RuntimeException("Stream, Degree, and Department are required for UG/PG");
                }

                StudentStream stream = streamRepository.findById(dto.getStreamId())
                        .orElseThrow(() -> new RuntimeException("Stream not found"));
                subject.setStream(stream);
                subject.setStreamName(stream.getStream());

                StudentDegreeName degree = degreeRepository.findById(dto.getDegreeId())
                        .orElseThrow(() -> new RuntimeException("Degree not found"));
                subject.setDegree(degree);
                subject.setDegreeName(degree.getDegreeName());

                StudentDepartment department = departmentRepository.findById(dto.getDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Department not found"));
                subject.setDepartment(department);
                subject.setDepartmentName(department.getDepartmentName());
            }
            else {
                throw new RuntimeException("Unsupported Graduation Type for College");
            }
        }
        else {
            throw new RuntimeException("Invalid institution type");
        }

        StudentSubject saved = subjectRepository.save(subject);

        // Prepare DTO response
        StudentSubjectDTO response = new StudentSubjectDTO();
        response.setId(saved.getId());
        response.setSubject(saved.getSubject());
        response.setCreatedByEmail(saved.getCreatedByEmail());
        response.setRole(saved.getRole());
        response.setBranchCode(saved.getBranchCode());
        response.setInstitutionType(saved.getInstitutionType());

        if (saved.getGraduationType() != null) {
            response.setGraduationTypeId(saved.getGraduationType().getId());
            response.setGraduationType(saved.getGraduationType().getGraduationType());
        }

        if (saved.getStream() != null) {
            response.setStreamId(saved.getStream().getId());
            response.setStream(saved.getStream().getStream());
        }

        if (saved.getDegree() != null) {
            response.setDegreeId(saved.getDegree().getId());
            response.setDegreeName(saved.getDegree().getDegreeName());
        }

        if (saved.getDepartment() != null) {
            response.setDepartmentId(saved.getDepartment().getId());
            response.setDepartmentName(saved.getDepartment().getDepartmentName());
        }

        return response;
    }


    @Override
    public StudentSubjectDTO getSubjectById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get subject");
        }
        StudentSubject subject = subjectRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Subject not found"));
        return mapToSubjectDTO(subject);

    }

    @Override
    public StudentSubject updateSubject(Long id,String role,String email,StudentSubject subject)
    {
        if(!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to update subject");
        }
        StudentSubject existingSubject = subjectRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Subject not found"));
        existingSubject.setSubject(subject.getSubject());
        return subjectRepository.save(existingSubject);

    }

    @Override
    public void deleteSubjectById(Long id, String role, String email) {
        if (!staffService.hasPermission(role, email, "Delete"))
            throw new RuntimeException("You don't have permission to delete subject");

        StudentSubject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        List<StudentTeacher> assignedTeachers = teacherRepository.findTeachersBySubjectId(subject.getId());

        if (!assignedTeachers.isEmpty()) {
            throw new RuntimeException("Subject is assigned to teacher(s). Please unassign it before deletion.");
        }

        subjectRepository.deleteById(id);
    }


    @Override
    public List<StudentSubjectDTO> getAllSubject(String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get subject");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        List<StudentSubject> subjects = subjectRepository.findAllByBranchCode(branchCode);
        return subjects.stream()
                .map(this::mapToSubjectDTO)
                .collect(Collectors.toList());

    }

    @Override
    public List<StudentSubjectDTO> getSubjects(String role, String email, String institutionType, String graduationTypeName, String streamName, String degreeName, String departmentName)
    {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to get subject");
        }


        String branchCode = staffService.fetchBranchCodeByRole(role, email);


        List<StudentSubject> subjects = subjectRepository.findSubjectsByFilters(
                branchCode,
                institutionType != null && !institutionType.trim().isEmpty() ? institutionType.trim() : null,
                graduationTypeName != null && !graduationTypeName.trim().isEmpty() ? graduationTypeName.trim() : null,
                streamName != null && !streamName.trim().isEmpty() ? streamName.trim() : null,
                degreeName != null && !degreeName.trim().isEmpty() ? degreeName.trim() : null,
                departmentName != null && !departmentName.trim().isEmpty() ? departmentName.trim() : null
        );


        return subjects.stream()
                .map(this::mapToSubjectDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<StudentSubjectDTO> getSubjectsByTeacherId(String role, String email,Long teacherId)
    {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to get subject");
        }

        List<StudentSubject> subjects = subjectRepository.findSubjectsByTeacherId(teacherId);
        return subjects.stream()
                .map(this::mapToSubjectDTO)
                .collect(Collectors.toList());
    }


    private StudentSubjectDTO mapToSubjectDTO(StudentSubject subject) {
        StudentSubjectDTO dto = new StudentSubjectDTO();

        dto.setId(subject.getId());
        dto.setSubject(subject.getSubject());
        dto.setCreatedByEmail(subject.getCreatedByEmail());
        dto.setRole(subject.getRole());
        dto.setBranchCode(subject.getBranchCode());
        dto.setInstitutionType(subject.getInstitutionType());

        // Graduation Type
        if (subject.getGraduationType() != null) {
            dto.setGraduationTypeId(subject.getGraduationType().getId());
            dto.setGraduationType(subject.getGraduationType().getGraduationType());
        }

        // Stream
        if (subject.getStream() != null) {
            dto.setStreamId(subject.getStream().getId());
            dto.setStream(subject.getStream().getStream());
        }

        if (subject.getDegree() != null) {
            dto.setDegreeId(subject.getDegree().getId());
            dto.setDegreeName(subject.getDegree().getDegreeName());
        }
        if (subject.getDepartment() != null) {
            dto.setDepartmentId(subject.getDepartment().getId());
            dto.setDepartmentName(subject.getDepartment().getDepartmentName());
        }

        return dto;
    }



}
