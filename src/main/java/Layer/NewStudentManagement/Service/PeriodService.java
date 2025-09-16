package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.StudentPeriodResponseDTO;
import Layer.NewStudentManagement.Entity.StudentPeriod;

import java.util.List;

public interface PeriodService
{

    StudentPeriodResponseDTO create(String role, String email, StudentPeriod period);
    StudentPeriodResponseDTO getById(String role, String email,Long id);
    List<StudentPeriodResponseDTO> getAll(String role, String email);
    StudentPeriodResponseDTO update(String role, String email,Long id, StudentPeriod period);
    void delete(String role, String email,Long id);
}
