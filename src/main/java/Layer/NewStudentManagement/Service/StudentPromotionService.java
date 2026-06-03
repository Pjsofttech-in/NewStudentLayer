package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.StudentBulkPromotionResponseDTO;
import Layer.NewStudentManagement.DTO.StudentPromotionResponseDTO;
import Layer.NewStudentManagement.Entity.StudentPromotionRecord;

import java.util.List;

public interface StudentPromotionService
{

    StudentPromotionResponseDTO promoteStudent(
            String role, String email, Long studentId,
            Long newStandardId, Long newMediumId,
            Long newDegreeNameId, String newDepartmentName,
            Long newStreamId, String groupName,
            String academicYear, Long newClassroomId,
            String institutionType, Long graduationTypeId );
    public StudentBulkPromotionResponseDTO promoteStudentList(
            String role, String email, List<Long> studentIdList,
            Long newStandardId, Long newMediumId,
            Long newDegreeNameId, String newDepartmentName,
            Long newStreamId, String groupName,
            String academicYear, Long newClassroomId,
            String institutionType, Long graduationTypeId);
    StudentPromotionResponseDTO getPromotionInfoById(String role, String email, Long studentId);
}

