package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StudentSubjectDTO;
import Layer.NewStudentManagement.Entity.*;

import Layer.NewStudentManagement.Repository.*;
import Layer.NewStudentManagement.Service.SubjectService;
import io.micrometer.common.util.StringUtils;
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

        //subject is now stored irrespective of graduation details

        StudentSubject saved = subjectRepository.save(subject);

        // Prepare DTO response
        StudentSubjectDTO response = new StudentSubjectDTO();
        response.setId(saved.getId());
        response.setSubject(saved.getSubject());
        response.setCreatedByEmail(saved.getCreatedByEmail());
        response.setRole(saved.getRole());
        response.setBranchCode(saved.getBranchCode());
        response.setInstitutionType(saved.getInstitutionType());

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
    public List<StudentSubjectDTO> getSubjects(String role, String email, String institutionType, String graduationTypeName, String streamName, String degreeName, String departmentName) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to get subject");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        List<StudentSubject> subjects = subjectRepository.findSubjectsByFilters(
                branchCode,
                institutionType != null && !institutionType.trim().isEmpty() ? institutionType.trim() : null
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

        return dto;
    }



}
