package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.StudentExamDTO;
import Layer.NewStudentManagement.Entity.StudentExam;

import java.util.List;

public interface ExamService
{
    StudentExamDTO createExamWithSubjects(StudentExam exam, List<Long> subjectIds, String role, String email);

    StudentExamDTO updateExamWithSubjects(Long id, StudentExam updated, List<Long> subjectIds, String role, String email);

    StudentExamDTO getExamById(Long id, String role, String email);

    List<StudentExamDTO> getExams(String role, String email);

    void deleteExam(Long id, String role, String email);

    void removeSubjectFromExam(Long examId, Long subjectId, String role, String email);
    List<StudentExamDTO> getExamsByClassId(Long classId,String role, String email);
}
