package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.*;
import Layer.NewStudentManagement.Entity.StudentFeesCollect;
import org.checkerframework.checker.nullness.qual.Nullable;
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
    List<Map<String, Object>> getReportByYear(String role, String email, @Nullable String branchCodeFilter);
    List<Map<String, Object>> getReportByMonth(String role, String email,int year,@Nullable String branchCodeFilter);
    List<Map<String, Object>> getReportByStandard(String role, String email, int academicYear);

    List<FeesByPaymentModeDTO> getCollectedFeesByPaymentMode(String role, String email,@Nullable String branchCode, String institutionType, Integer year);
    Map<String, Double> getFeesRevenueByBank(String role,String email,@Nullable String branchCodeFilter);
    Page<StudentFeesHistoryDTO> getAllCollectedFeesByBranch(
            String role, String email, FeesFilterDTO filterDTO, String timeFrame, LocalDate startDate, LocalDate endDate, int page, int size,
            String sort);

    Map<String, Object> getDailyCollectedFees(String role, String email, LocalDate date);
    Map<String, Object> getCollectedFeesByFilter(String role, String email, String filter,
                                                 LocalDate fromDate, LocalDate toDate);
    Map<String, List<FeesScheduleChartProjection>> getReportByDayInMonth(String role, String email, int year, String monthName,
                                                                                String branchCodeFilter);

}
