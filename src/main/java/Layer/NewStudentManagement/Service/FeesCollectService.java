package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.*;
import Layer.NewStudentManagement.Entity.StudentFeesCollect;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FeesCollectService
{
    FeesCollectDTO saveFeeCollection(StudentFeesCollect collect, String role, String email);
    FeesCollectDTO updateFeeCollectionStatus(Long id, String role, String email, String newStatus);
    List<FeesCollectDTO> getAllCollectDataByStudentFeesID(Long fid,String role,String email);
    List<FeesCollectDTO> getCollectedFeesByStudentId(String role, String email,Long studentId);
    FeesCollectDTO getCollectedFeesById(String role, String email, Long id);
    List<Map<String, Object>> getReportByYear(String role, String email);
    List<Map<String, Object>> getReportByMonth(String role, String email,int year);
    List<Map<String, Object>> getReportByStandard(String role, String email);

    List<FeesByPaymentModeDTO> getCollectedFeesByPaymentMode(String role, String email, String institutionType);
    Map<String, Double> getFeesRevenueByBank(String role,String email);
    Page<StudentFeesHistoryDTO> getAllCollectedFeesByBranch(String role, String email,
                                                            FeesFilterDTO filterDTO, int page, int size);
}
