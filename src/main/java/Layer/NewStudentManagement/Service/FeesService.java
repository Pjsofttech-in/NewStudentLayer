package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.*;
import Layer.NewStudentManagement.Entity.StudentFees;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FeesService
{
    StudentFeesDTO assignFeesToStudent(String role, String email, StudentFees fees);
    StudentFeesDTO updateFees(Long id, StudentFees updatedFees,String role, String email);
    void deleteFees(Long id,String role, String email);
    StudentFeesDTO getFeesById(Long id,String role, String email);
//    List<StudentFeesDTO> getAllFees(String role, String email);
    List<StudentFeesDTO> getAllFeesForStudent(Long studentId,String role, String email);
    Page<StudentFeesDTO> getAllFeesWithFilter(FeesFilterDTO filterDTO, String branchCode, int page, int size);
    FeesRevenueProjection getFeesRevenueByBranch(String role, String email, String timeFrame, LocalDate startDate, LocalDate endDate, FeesRevenueFilterDTO filters);
    FeesRevenueProjection getFeesRevenueByStudentId(String role, String email,Long studentId);
    Map<String, Object> getMonthlyFeesStatus(String role, String email, String month, @Nullable String branchCodeFilter);
}
