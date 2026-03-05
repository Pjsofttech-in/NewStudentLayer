package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.SubjectMarksDTO;
import Layer.NewStudentManagement.Entity.StudentClassRoom;
import Layer.NewStudentManagement.Entity.StudentSubjectMarks;
import Layer.NewStudentManagement.Enum.Role;
import Layer.NewStudentManagement.Repository.ClassRoomRepository;
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

    @Autowired
    ClassRoomRepository classRoomRepository;

    @Override
    @Transactional
    public SubjectMarksDTO createSubject(StudentSubjectMarks subject, String role, String email) {
        if (!staffService.hasPermission(role, email, "POST")) {
            throw new RuntimeException("You don't have permission to create Subject");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        subject.setRole(Role.valueOf(role));
        subject.setCreatedByEmail(email);
        subject.setBranchCode(branchCode);
        StudentClassRoom classRoom = classRoomRepository.findById(subject.getClassRoom().getId())
                .orElseThrow(() -> new RuntimeException("ClassRoom not found with id: " + subject.getClassRoom().getId()));
        subject.setClassRoom(classRoom);
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
    public List<SubjectMarksDTO> getSubjectsByClassroomId(Long classroomId, String role, String email) {
        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to view Subjects");
        }

        List<StudentSubjectMarks> subjects = subjectMarksRepository.findByClassRoomId(classroomId);

        if (subjects.isEmpty()) {
            throw new RuntimeException("No subjects found for classroom id: " + classroomId);
        }

        return subjects.stream()
                .map(this::subjectMapToDto)
                .toList();
    }

    @Override
    @Transactional
    public SubjectMarksDTO updateSubject(Long id, StudentSubjectMarks updated, String role, String email) {
        if (!staffService.hasPermission(role, email, "PUT")) {
            throw new RuntimeException("You don't have permission to update Subject");
        }

        StudentSubjectMarks existing = subjectMarksRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        if (updated.getSubjectName() != null && !updated.getSubjectName().isEmpty()) {
            existing.setSubjectName(updated.getSubjectName());
        }

        if (updated.getMaxMarks() != null) {
            existing.setMaxMarks(updated.getMaxMarks());
        }

        if (updated.getPassingMarks() != null) {
            existing.setPassingMarks(updated.getPassingMarks());
        }
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



    @Override
    public List<SubjectMarksDTO> getSubjectsByExamId(String role, String email,Long examId)
    {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Get Subject");
        }
        List<StudentSubjectMarks> subjects = subjectMarksRepository.findSubjectsByExamId(examId);
        return subjects.stream()
                .map(this::subjectMapToDto)
                .toList();
    }

    private SubjectMarksDTO subjectMapToDto(StudentSubjectMarks subject) {
        SubjectMarksDTO dto = new SubjectMarksDTO();
        dto.setId(subject.getId());
        dto.setSubjectName(subject.getSubjectName());
        dto.setMaxMarks(subject.getMaxMarks());
        dto.setPassingMarks(subject.getPassingMarks());
        dto.setCreatedByEmail(subject.getCreatedByEmail());
        if (subject.getClassRoom() != null) {
            dto.setClassRoomId(subject.getClassRoom().getId());
        } else {
            dto.setClassRoomId(null);
        }
        dto.setRole(subject.getRole());
        dto.setBranchCode(subject.getBranchCode());
        return dto;
    }
}
