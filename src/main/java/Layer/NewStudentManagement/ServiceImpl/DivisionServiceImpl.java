package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StudentDivisionDTO;
import Layer.NewStudentManagement.Entity.StudentDivision;

import Layer.NewStudentManagement.Repository.DivisionRepository;
import Layer.NewStudentManagement.Service.DivisionService;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DivisionServiceImpl implements DivisionService
{
    @Autowired
    private StaffService staffService;

    @Autowired
    private DivisionRepository divisionRepository;

    @Override
    public StudentDivision createDivision(String role, String email, StudentDivision division)
    {
        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create division");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        if (divisionRepository.existsByDivisionIgnoreCaseAndBranchCode(division.getDivision(), branchCode)) {
            throw new RuntimeException("Medium already exists for this branch");
        }
        division.setBranchCode(branchCode);
        division.setRole(role);
        division.setCreatedByEmail(email);
        return divisionRepository.save(division);
    }
    @Override
    public StudentDivisionDTO getDivisionById(Long id, String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get division");
        }
        StudentDivision division = divisionRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Division not found"));
        return mapToDivisionDTO(division);
    }

    @Override
    public StudentDivision updateDivision(Long id,String role,String email,StudentDivision division)
    {
        if(!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to update division");
        }
        StudentDivision existingDivision = divisionRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Division not found"));
        existingDivision.setDivision(division.getDivision());
        return divisionRepository.save(existingDivision);
    }

    @Override
    public void deleteDivisionById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Delete"))
        {
            throw new RuntimeException("You don't have permission to delete division");
        }
        divisionRepository.deleteById(id);
    }

    @Override
    public List<StudentDivisionDTO> getAllDivision(String role, String email, String branchCode) {

        try {

            if ("STUDENT".equalsIgnoreCase(role) || "USER".equalsIgnoreCase(role) || "TEACHER".equalsIgnoreCase(role)) {

                // 🔥 FIX: auto-fetch branchCode
                if (branchCode == null || branchCode.isBlank()) {
                    branchCode = staffService.fetchBranchCodeByRole(role, email);
                }

                if (branchCode == null || branchCode.isBlank()) {
                    throw new RuntimeException("BranchCode not found");
                }

                return divisionRepository.findAllByBranchCode(branchCode).stream()
                        .map(this::mapToDivisionDTO)
                        .collect(Collectors.toList());
            }

            // ✅ SUPERADMIN
            if ("SUPERADMIN".equalsIgnoreCase(role)) {

                if (!staffService.hasPermission(role, email, "GET")) {
                    throw new RuntimeException("You don't have permission to get Division");
                }

                List<String> instituteBranchCodes =
                        staffService.getBranchCodesByInstituteEmail(email);

                if (instituteBranchCodes == null || instituteBranchCodes.isEmpty()) {
                    throw new RuntimeException("No branches found for this institute email: " + email);
                }

                return divisionRepository.findAllByBranchCodeIn(instituteBranchCodes).stream()
                        .map(this::mapToDivisionDTO)
                        .collect(Collectors.toList());
            }

            // ✅ OTHER ROLES (ADMIN / STAFF)
            if (!staffService.hasPermission(role, email, "GET")) {
                throw new RuntimeException("You don't have permission to get Division");
            }

            String resolvedBranch =
                    staffService.fetchBranchCodeByRole(role, email);

            if (resolvedBranch == null || resolvedBranch.isBlank()) {
                throw new RuntimeException("BranchCode not found for role: " + role);
            }

            return divisionRepository.findAllByBranchCode(resolvedBranch).stream()
                    .map(this::mapToDivisionDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private StudentDivisionDTO mapToDivisionDTO(StudentDivision division) {
        return new StudentDivisionDTO(
                division.getDid(),
                division.getDivision(),
                division.getCreatedByEmail(),
                division.getRole(),
                division.getBranchCode()
        );
    }

}
