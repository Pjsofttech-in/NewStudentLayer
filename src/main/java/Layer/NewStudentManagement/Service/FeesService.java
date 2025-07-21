package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.FeesRevenueProjection;
import Layer.NewStudentManagement.DTO.StudentFeesDTO;
import Layer.NewStudentManagement.DTO.StudentFeesFilterRequest;
import Layer.NewStudentManagement.Entity.StudentFees;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FeesService
{
    StudentFeesDTO assignFeesToStudent(String role, String email, StudentFees fees);
    StudentFeesDTO updateFees(Long id, StudentFees updatedFees,String role, String email);
    void deleteFees(Long id,String role, String email);
    StudentFeesDTO getFeesById(Long id,String role, String email);
//    List<StudentFeesDTO> getAllFees(String role, String email);
    List<StudentFeesDTO> getAllFeesForStudent(Long studentId,String role, String email);
    Page<StudentFeesDTO> filterStudentFees(StudentFeesFilterRequest request,String role, String email, int page, int size);

    FeesRevenueProjection getFeesRevenueByBranch(String role, String email);
}
