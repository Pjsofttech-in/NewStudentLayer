package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StudentSubjectDTO;
import Layer.NewStudentManagement.Entity.StudentGraduationType;
import Layer.NewStudentManagement.Entity.StudentStream;
import Layer.NewStudentManagement.Entity.StudentSubject;
import Layer.NewStudentManagement.Entity.StudentTeacher;
import Layer.NewStudentManagement.Repository.GraduationTypeRepository;
import Layer.NewStudentManagement.Repository.StreamRepository;
import Layer.NewStudentManagement.Repository.SubjectRepository;
import Layer.NewStudentManagement.Repository.TeacherRepository;
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


    @Override
    public StudentSubjectDTO saveSubject(StudentSubjectDTO dto, String role, String email) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to create subject");
        }

        StudentSubject subject = new StudentSubject();
        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        subject.setBranchCode(branchCode);
        subject.setRole(role);
        subject.setCreatedByEmail(email);
        subject.setInstitutionType(dto.getInstitutionType());
        subject.setSubject(dto.getSubject());

        // Graduation Type Mapping
        if (dto.getGraduationTypeId() != null) {
            StudentGraduationType graduationType = graduationTypeRepository.findById(dto.getGraduationTypeId())
                    .orElseThrow(() -> new RuntimeException("Graduation Type not found"));
            subject.setGraduationType(graduationType);
            subject.setGraduationTypeName(graduationType.getGraduationType());
        }

        // Stream Mapping
        if (dto.getStreamId() != null) {
            StudentStream stream = streamRepository.findById(dto.getStreamId())
                    .orElseThrow(() -> new RuntimeException("Stream not found"));
            subject.setStream(stream);
            subject.setStreamName(stream.getStream());
        }

        StudentSubject saved = subjectRepository.save(subject);

        // Return safe DTO (not entity) to avoid deep nesting
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
    public List<StudentSubjectDTO> getSubjects(String role, String email, String institutionType, String graduationTypeName, String streamName)
    {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to get subject");
        }

        if (institutionType == null || institutionType.trim().isEmpty()) {
            return Collections.emptyList(); // no need to query if key input is missing
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        // School doesn't require graduation or stream filters
        if ("School".equalsIgnoreCase(institutionType)) {
            graduationTypeName = null;
            streamName = null;
        }

        List<StudentSubject> subjects = subjectRepository.findByFilters(
                branchCode,
                institutionType.trim(),
                graduationTypeName != null && !graduationTypeName.trim().isEmpty() ? graduationTypeName.trim() : null,
                streamName != null && !streamName.trim().isEmpty() ? streamName.trim() : null
        );

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

        if (subject.getGraduationType() != null) {
            dto.setGraduationType(subject.getGraduationTypeName());
            dto.setGraduationTypeId(subject.getGraduationType().getId());

            if (subject.getGraduationType().getStream() != null) {
                dto.setStreamId(subject.getGraduationType().getStream().getId());
                dto.setStream(subject.getGraduationType().getStream().getStream());
            }
        }

        return dto;
    }


}
