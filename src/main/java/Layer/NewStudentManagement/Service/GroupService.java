package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.StudentGroupDTO;
import Layer.NewStudentManagement.Entity.StudentGroup;
import jakarta.annotation.Nullable;

import java.util.List;

public interface GroupService
{
    StudentGroupDTO createGroup(String role, String email, StudentGroup group);
    StudentGroupDTO getGroupById(Long id,String role,String email);
    StudentGroupDTO updateGroup(Long id,String role,String email,StudentGroup group);
    void deleteGroupById(Long id,String role,String email);
    List<StudentGroupDTO> getAllGroupByName(String role, String email, @Nullable String branchCode, String token);
    List<StudentGroupDTO> getGroupsByGraduationTypeId(String role, String email,Long graduationTypeId);
}
