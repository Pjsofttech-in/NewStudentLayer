package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.StudentPromotionResponseDTO;
import Layer.NewStudentManagement.Entity.StudentPromotionRecord;

import java.util.List;

public interface StudentPromotionService
{

     StudentPromotionResponseDTO promoteStudent(String role, String email, Long studentId, Long newStandardId,
                                                      Long newMediumId, Long newDegreeNameId, Long newDepartmentId, Long newStreamId, String groupName, String academicYear, Long newClassroomId);
    StudentPromotionResponseDTO getPromotionInfoById(String role, String email, Long studentId);
}

