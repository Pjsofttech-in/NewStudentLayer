package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.ExamSubjectDto;
import Layer.NewStudentManagement.DTO.StudentExamDTO;
import Layer.NewStudentManagement.Entity.StudentExam;
import Layer.NewStudentManagement.Entity.StudentExamSubject;
import Layer.NewStudentManagement.Entity.StudentSubjectMarks;
import Layer.NewStudentManagement.Repository.ExamRepository;
import Layer.NewStudentManagement.Repository.ExamSubjectRepository;
import Layer.NewStudentManagement.Repository.SubjectMarksRepository;
import Layer.NewStudentManagement.Service.ExamService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl implements ExamService
{

    @Autowired
    ExamRepository examRepository;

    @Autowired
    ExamSubjectRepository examSubjectRepository;

    @Autowired
    StaffService staffService;

    @Autowired
    SubjectMarksRepository subjectMarksRepository;

    @Override
    @Transactional
    public StudentExamDTO createExamWithSubjects(StudentExam exam, List<Long> subjectIds, String role, String email) {
        if (!staffService.hasPermission(role, email, "POST")) {
            throw new RuntimeException("You don't have permission to create Exam");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        exam.setRole(role);
        exam.setCreatedByEmail(email);
        exam.setBranchCode(branchCode);


        List<StudentExamSubject> examSubjects = new ArrayList<>();
        for (Long subjectId : subjectIds) {
            StudentSubjectMarks subject = subjectMarksRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Subject not found: " + subjectId));

            StudentExamSubject examSubject = new StudentExamSubject();
            examSubject.setExam(exam);
            examSubject.setSubject(subject);
            examSubject.setRole(role);
            examSubject.setCreatedByEmail(email);
            examSubject.setBranchCode(branchCode);

            examSubjects.add(examSubject);
        }

        exam.setSubjects(examSubjects);
        StudentExam exam1 =examRepository.save(exam);
        return exammapToDto(exam1);
    }

    @Override
    @Transactional
    public StudentExamDTO updateExamWithSubjects(Long id, StudentExam updated, List<Long> subjectIds, String role, String email) {
        if (!staffService.hasPermission(role, email, "PUT")) {
            throw new RuntimeException("You don't have permission to update Exam");
        }

        StudentExam existing = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        if (updated.getExamName() != null) {
            existing.setExamName(updated.getExamName());
        }
        if (updated.getExamDate() != null) {
            existing.setExamDate(updated.getExamDate());
        }
        if (updated.getExamType() != null) {
            existing.setExamType(updated.getExamType());
        }
        if (updated.getClassRoom() != null) {
            existing.setClassRoom(updated.getClassRoom());
        }

        if (subjectIds != null && !subjectIds.isEmpty()) {
            existing.getSubjects().clear();

            List<StudentExamSubject> newSubjects = new ArrayList<>();
            for (Long subjectId : subjectIds) {
                var subject = subjectMarksRepository.findById(subjectId)
                        .orElseThrow(() -> new RuntimeException("Subject not found: " + subjectId));

                StudentExamSubject examSubject = new StudentExamSubject();
                examSubject.setExam(existing);
                examSubject.setSubject(subject);
                examSubject.setRole(role);
                examSubject.setCreatedByEmail(email);
                examSubject.setBranchCode(existing.getBranchCode());

                newSubjects.add(examSubject);
            }
            existing.getSubjects().addAll(newSubjects);
        }

        StudentExam exam = examRepository.save(existing);
        return exammapToDto(exam);
    }


    @Override
    public StudentExamDTO getExamById(Long id, String role, String email) {
        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to view Exam");
        }
        StudentExam exams = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        return exammapToDto(exams);
    }

    @Override
    public List<StudentExamDTO> getExams(String role, String email) {
        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to view Exams");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        List<StudentExam> exams = examRepository.findExamByBranchCode(branchCode);

        return exams.stream()
                .map(this::exammapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteExam(Long id, String role, String email) {
        if (!staffService.hasPermission(role, email, "DELETE")) {
            throw new RuntimeException("You don't have permission to delete Exam");
        }

        StudentExam existing = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        if (existing.getSubjects() != null && !existing.getSubjects().isEmpty()) {
            throw new RuntimeException("Cannot delete Exam. Delete assigned ExamSubjects first.");
        }

        examRepository.delete(existing);
    }

    @Override
    @Transactional
    public void removeSubjectFromExam(Long examId, Long subjectId, String role, String email) {
        if (!staffService.hasPermission(role, email, "DELETE")) {
            throw new RuntimeException("You don't have permission to remove ExamSubject");
        }

        StudentExamSubject examSubject = examSubjectRepository
                .findByExamIdAndSubjectId(examId, subjectId)
                .orElseThrow(() -> new RuntimeException("ExamSubject not found"));

        examSubjectRepository.delete(examSubject);
    }

    public StudentExamDTO exammapToDto(StudentExam exam) {
        StudentExamDTO dto = new StudentExamDTO();
        dto.setId(exam.getId());
        dto.setExamName(exam.getExamName());
        dto.setExamDate(exam.getExamDate());
        dto.setExamType(exam.getExamType());
        dto.setCreatedByEmail(exam.getCreatedByEmail());
        dto.setRole(exam.getRole());
        dto.setBranchCode(exam.getBranchCode());

        if (exam.getClassRoom() != null) {
            dto.setClassRoomId(exam.getClassRoom().getId());
        }

        if (exam.getSubjects() != null) {
            List<ExamSubjectDto> subjectDtos = exam.getSubjects().stream()
                    .map(sub -> {
                        ExamSubjectDto s = new ExamSubjectDto();
                        s.setId(sub.getId());
                        s.setSubjectId(sub.getSubject().getId());
                        s.setSubjectName(sub.getSubject().getSubjectName());
                        s.setMaxMarks(sub.getSubject().getMaxMarks());
                        return s;
                    }).toList();
            dto.setSubjects(subjectDtos);
        }

        return dto;
    }
}
