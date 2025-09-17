package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StudentPeriodResponseDTO;
import Layer.NewStudentManagement.Entity.StudentPeriod;
import Layer.NewStudentManagement.Exception.ResourceNotFoundException;
import Layer.NewStudentManagement.Repository.PeriodRepository;
import Layer.NewStudentManagement.Service.PeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeriodServiceImpl implements PeriodService
{
    @Autowired
    PeriodRepository periodRepository;

    @Autowired
    StaffService staffService;

    @Override
    public StudentPeriodResponseDTO createPeriod(String role, String email, StudentPeriod period) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to create Period");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        period.setRole(role);
        period.setCreatedByEmail(email);
        period.setBranchCode(branchCode);

        StudentPeriod savedPeriod = periodRepository.save(period);

        return convertToDTO(savedPeriod);
    }


    @Override
    public StudentPeriodResponseDTO getPeriodById(String role, String email,Long id)
    {

        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to View Period");
        }
        StudentPeriod period = periodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Period not found with id " + id));
        return convertToDTO(period);
    }

    @Override
    public List<StudentPeriodResponseDTO> getAllPeriod(String role, String email)
    {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to View Period");
        }
        String branchCode =staffService.fetchBranchCodeByRole(role, email);
        return periodRepository.getAllByBranchCode(branchCode)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public StudentPeriodResponseDTO updatePeriod(String role, String email, Long id, StudentPeriod period) {
        if (!staffService.hasPermission(role, email, "Put")) {
            throw new RuntimeException("You don't have permission to update Period");
        }

        StudentPeriod existing = periodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Period not found with id " + id));

        // Update only if new values are provided
        if (period.getPeriodNo() != null) {
            existing.setPeriodNo(period.getPeriodNo());
        }
        if (period.getStartTime() != null) {
            existing.setStartTime(period.getStartTime());
        }
        if (period.getEndTime() != null) {
            existing.setEndTime(period.getEndTime());
        }

        StudentPeriod saved = periodRepository.save(existing);
        return convertToDTO(saved);
    }


    @Override
    public void deletePeriod(String role, String email,Long id)
    {
        if (!staffService.hasPermission(role, email, "Delete")) {
            throw new RuntimeException("You don't have permission to Delete Period");
        }
        StudentPeriod existing = periodRepository.findById(id).orElseThrow(() -> new RuntimeException("Period Not Found"));
        periodRepository.delete(existing);
    }


    private StudentPeriodResponseDTO convertToDTO(StudentPeriod period) {
        StudentPeriodResponseDTO dto = new StudentPeriodResponseDTO();
        dto.setId(period.getId());
        dto.setPeriodNo(period.getPeriodNo());
        dto.setStartTime(period.getStartTime() != null ? period.getStartTime().toString() : null);
        dto.setEndTime(period.getEndTime() != null ? period.getEndTime().toString() : null);

        dto.setCreatedByEmail(period.getCreatedByEmail());
        dto.setBranchCode(period.getBranchCode());
        dto.setRole(period.getRole());

        return dto;
    }


}
