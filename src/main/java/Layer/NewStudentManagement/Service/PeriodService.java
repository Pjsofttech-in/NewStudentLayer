package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.StudentPeriodResponseDTO;
import Layer.NewStudentManagement.Entity.StudentPeriod;

import java.util.List;

public interface PeriodService
{

    StudentPeriodResponseDTO createPeriod(String role, String email, StudentPeriod period);
    StudentPeriodResponseDTO getPeriodById(String role, String email,Long id);
    List<StudentPeriodResponseDTO> getAllPeriod(String role, String email);
    StudentPeriodResponseDTO updatePeriod(String role, String email,Long id, StudentPeriod period);
    void deletePeriod(String role, String email,Long id);
}
