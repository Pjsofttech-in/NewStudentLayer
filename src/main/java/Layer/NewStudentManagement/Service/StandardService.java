package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.StandardDTO;
import Layer.NewStudentManagement.Entity.StudentStandard;

import java.util.List;

public interface StandardService
{
    StudentStandard createStandard(String role,String email,StudentStandard standard);
    StandardDTO getStandardById(Long id, String role, String email);
    StandardDTO updateStandard(Long id, String role, String email, StandardDTO standardDTO);
    void deleteStandardById(Long id,String role,String email);
    List<StandardDTO> getAllStandard(String role, String email,String token);
}
