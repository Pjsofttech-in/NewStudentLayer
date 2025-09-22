package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.SubjectMarksDTO;
import Layer.NewStudentManagement.Entity.StudentSubjectMarks;
import Layer.NewStudentManagement.Repository.SubjectMarksRepository;
import Layer.NewStudentManagement.Service.SubjectMarksService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectMarksServiceImpl implements SubjectMarksService
{

    @Autowired
    SubjectMarksRepository subjectMarksRepository;

    @Autowired
    StaffService staffService;


    @Override
    @Transactional
    public SubjectMarksDTO createSubject(StudentSubjectMarks subject, String role, String email) {
        if (!staffService.hasPermission(role, email, "POST")) {
            throw new RuntimeException("You don't have permission to create Subject");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        subject.setRole(role);
        subject.setCreatedByEmail(email);
        subject.setBranchCode(branchCode);

        StudentSubjectMarks subjectMarks = subjectMarksRepository.save(subject);
        return subjectMapToDto(subjectMarks);
    }

    @Override
    public List<SubjectMarksDTO> getSubjects(String role, String email) {
        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to view Subjects");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        List<StudentSubjectMarks> subjectMarks = subjectMarksRepository.findSubjectByBranchCode(branchCode);

        return subjectMarks.stream()
                .map(this::subjectMapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SubjectMarksDTO getSubjectsById(Long id,String role, String email) {
        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to view Subjects");
        }
        StudentSubjectMarks existing = subjectMarksRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        return subjectMapToDto(existing);
    }

    @Override
    @Transactional
    public SubjectMarksDTO updateSubject(Long id, StudentSubjectMarks updated, String role, String email) {
        if (!staffService.hasPermission(role, email, "PUT")) {
            throw new RuntimeException("You don't have permission to update Subject");
        }

        StudentSubjectMarks existing = subjectMarksRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        existing.setSubjectName(updated.getSubjectName());
        existing.setMaxMarks(updated.getMaxMarks());
        StudentSubjectMarks subjectMarks = subjectMarksRepository.save(existing);
        return subjectMapToDto(subjectMarks);
    }

    @Override
    @Transactional
    public void deleteSubject(Long id, String role, String email) {
        if (!staffService.hasPermission(role, email, "DELETE")) {
            throw new RuntimeException("You don't have permission to delete Subject");
        }

        StudentSubjectMarks existing = subjectMarksRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        subjectMarksRepository.delete(existing);
    }

    private SubjectMarksDTO subjectMapToDto(StudentSubjectMarks subject) {
        SubjectMarksDTO dto = new SubjectMarksDTO();
        dto.setId(subject.getId());
        dto.setSubjectName(subject.getSubjectName());
        dto.setMaxMarks(subject.getMaxMarks());
        dto.setCreatedByEmail(subject.getCreatedByEmail());
        dto.setRole(subject.getRole());
        dto.setBranchCode(subject.getBranchCode());
        return dto;
    }
}
