package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StandardDTO;
import Layer.NewStudentManagement.Entity.StudentStandard;

import Layer.NewStudentManagement.Repository.StandardRepository;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.StandardService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StandardServiceImpl implements StandardService
{

    @Autowired
    private StandardRepository standardRepository;

    @Autowired
    private StaffService staffService;

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public StudentStandard createStandard(String role, String email, StudentStandard standard)
    {
        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create standard");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        if (standardRepository.existsByStandardNameIgnoreCaseAndBranchCode(standard.getStandardName(), branchCode)) {
            throw new RuntimeException("Standard already exists for this branch");
        }
        standard.setBranchCode(branchCode);
        standard.setRole(role);
        standard.setCreatedByEmail(email);

        return standardRepository.save(standard);
    }

    @Override
    public StandardDTO getStandardById(Long id, String role, String email){
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get standard");
        }
        StudentStandard standard = standardRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Standard not found"));
        return mapToStandardDTO(standard);
    }

    @Override
    public StandardDTO updateStandard(Long id, String role, String email, StandardDTO standardDTO) {
        if (!staffService.hasPermission(role, email, "Put")) {
            throw new RuntimeException("You don't have permission to update standard");
        }

        StudentStandard existingStandard = standardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Standard not found"));

        existingStandard.setStandardName(standardDTO.getStandardName());

        StudentStandard updatedStandard = standardRepository.save(existingStandard);

        return mapToStandardDTO(updatedStandard);
    }


    @Override
    public void deleteStandardById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Delete"))
        {
            throw new RuntimeException("You don't have permission to delete standard");
        }
        standardRepository.deleteById(id);
    }

    @Override
    public List<StandardDTO> getAllStandard(String role, String email, String branchCode) {

        try {

            if ("STUDENT".equalsIgnoreCase(role) || "USER".equalsIgnoreCase(role) || "TEACHER".equalsIgnoreCase(role)) {

                // 🔥 FIX: auto-fetch branchCode
                if (branchCode == null || branchCode.isBlank()) {
                    branchCode = staffService.fetchBranchCodeByRole(role, email);
                }

                if (branchCode == null || branchCode.isBlank()) {
                    throw new RuntimeException("BranchCode not found");
                }

                return standardRepository.getAllStandardByBranchCode(branchCode).stream()
                        .map(this::mapToStandardDTO)
                        .collect(Collectors.toList());
            }

            // ✅ SUPERADMIN
            if ("SUPERADMIN".equalsIgnoreCase(role)) {

                if (!staffService.hasPermission(role, email, "GET")) {
                    throw new RuntimeException("You don't have permission to get Standard");
                }

                List<String> instituteBranchCodes =
                        staffService.getBranchCodesByInstituteEmail(email);

                if (instituteBranchCodes == null || instituteBranchCodes.isEmpty()) {
                    throw new RuntimeException("No branches found for this institute email: " + email);
                }

                return standardRepository.getAllStandardByBranchCodeIn(instituteBranchCodes).stream()
                        .map(this::mapToStandardDTO)
                        .collect(Collectors.toList());
            }

            // ✅ OTHER ROLES (ADMIN / STAFF)
            if (!staffService.hasPermission(role, email, "GET")) {
                throw new RuntimeException("You don't have permission to get Standard");
            }

            String resolvedBranch =
                    staffService.fetchBranchCodeByRole(role, email);

            if (resolvedBranch == null || resolvedBranch.isBlank()) {
                throw new RuntimeException("BranchCode not found for role: " + role);
            }

            return standardRepository.getAllStandardByBranchCode(resolvedBranch).stream()
                    .map(this::mapToStandardDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }



    private StandardDTO mapToStandardDTO(StudentStandard standard) {
        return new StandardDTO(
                standard.getSid(),
                standard.getStandardName(),
                standard.getCreatedByEmail(),
                standard.getRole(),
                standard.getBranchCode()
        );
    }


}
