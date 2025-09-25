package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.SubjectMarksDTO;
import Layer.NewStudentManagement.Entity.StudentSubjectMarks;

import java.util.List;

public interface SubjectMarksService
{
    SubjectMarksDTO createSubject(StudentSubjectMarks subject, String role, String email);
    List<SubjectMarksDTO> getSubjects(String role, String email);
    SubjectMarksDTO updateSubject(Long id, StudentSubjectMarks updated, String role, String email);
    void deleteSubject(Long id, String role, String email);
    SubjectMarksDTO getSubjectsById(Long id, String role, String email);
    List<SubjectMarksDTO> getSubjectsByClassroomId(Long classroomId, String role, String email);
    List<SubjectMarksDTO> getSubjectsByExamId(String role, String email,Long examId);
}
