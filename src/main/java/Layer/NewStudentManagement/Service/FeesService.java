package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.StudentFeesDTO;
import Layer.NewStudentManagement.Entity.StudentFees;

import java.util.List;

public interface FeesService
{
    StudentFeesDTO assignFeesToStudent(String role, String email, StudentFees fees);
    StudentFeesDTO updateFees(Long id, StudentFees updatedFees,String role, String email);
    void deleteFees(Long id,String role, String email);
    StudentFeesDTO getFeesById(Long id,String role, String email);
    List<StudentFeesDTO> getAllFees(String role, String email);

}
