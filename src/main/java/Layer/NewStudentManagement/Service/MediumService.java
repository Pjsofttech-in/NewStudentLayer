package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.MediumDTO;
import Layer.NewStudentManagement.Entity.StudentMedium;

import java.util.List;

public interface MediumService
{
    StudentMedium createMedium(String role, String email, StudentMedium medium);
    MediumDTO getMediumById(Long id, String role, String email);
    StudentMedium updateMedium(Long id,String role,String email,StudentMedium medium);
    void deleteMediumById(Long id,String role,String email);
    List<MediumDTO> getAllMedium(String role, String email,String token);
}
