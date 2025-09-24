package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.StudentResultDTO;
import Layer.NewStudentManagement.Entity.StudentResult;

import java.util.List;

public interface ResultService
{
    StudentResultDTO createResult(StudentResult result, String role, String email);
    StudentResultDTO updateResult(Long id, StudentResult updated, String role, String email);
    void deleteResult(Long id, String role, String email);
    List<StudentResultDTO> getAllResults(String role, String email);
    StudentResultDTO getResultById(Long id, String role, String email);
    List<StudentResultDTO> getAcademicYearResults(String role, String email,Long studentId, String academicYear);
    StudentResultDTO getLatestResultByStudentId(Long studentId, String role, String email);
//    List<StudentResultDTO> getResultsByClassRoom(Long classRoomId, String role, String email);
    StudentResultDTO submitMark(Long studentId, Long examId, Long subjectId,
                                Integer obtainedMarks, String role, String email);
    List<StudentResultDTO> getResultsByClassRoom(String role, String email,Long examId, Long classRoomId);
}
