package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.Entity.StudentEntranceExam;
import jakarta.annotation.Nullable;

import java.util.List;

public interface EntranceExamService
{
    StudentEntranceExam createIntranceExam(String role, String email,StudentEntranceExam exam);

    List<StudentEntranceExam> getAllIntranceExams(String role, String email,
                                                  @Nullable String branchCode, String token);

    StudentEntranceExam getIntranceExamById(String role, String email,Long id);

    StudentEntranceExam updateIntranceExam(Long id, String role, String email,StudentEntranceExam exam);

    String deleteIntranceExam(Long id,String role, String email);


}
