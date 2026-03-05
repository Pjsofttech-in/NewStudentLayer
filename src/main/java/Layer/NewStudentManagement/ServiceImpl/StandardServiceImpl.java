package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StandardDTO;
import Layer.NewStudentManagement.Entity.StudentStandard;
import Layer.NewStudentManagement.Enum.Role;
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
        standard.setRole(Role.valueOf(role));
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
    public List<StandardDTO> getAllStandard(String role, String email, String token, String branchCode)
    {
        String branchCodeToUse;

        if ("USER".equalsIgnoreCase(role)) {
            Claims claims = jwtUtil.extractAllClaims(token);
            String encoded = claims.get("branchCode", String.class);

            if (encoded == null || encoded.isEmpty()) {
                throw new RuntimeException("Invalid token: branchCode not found");
            }

            branchCodeToUse = new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);

            List<StudentStandard> standards = standardRepository.getAllStandardByBranchCode(branchCodeToUse);
            return standards.stream()
                    .map(this::mapToStandardDTO)
                    .collect(Collectors.toList());
        }

        if ("SUPERADMIN".equalsIgnoreCase(role)) {
            boolean hasPerm = staffService.hasPermission(role, email, "GET");
            if (!hasPerm) {
                throw new RuntimeException("You don't have permission or email does not exist for SuperAdmin");
            }

            List<String> instituteBranchCodes = staffService.getBranchCodesByInstituteEmail(email);
            if (instituteBranchCodes == null || instituteBranchCodes.isEmpty()) {
                return Collections.emptyList();
            }

            if (branchCode != null && !branchCode.isBlank()) {
                String requested = branchCode.trim();
                if (instituteBranchCodes.contains(requested)) {
                    instituteBranchCodes = Collections.singletonList(requested);
                } else {
                    return Collections.emptyList();
                }
            }

            List<StudentStandard> standards = standardRepository.getAllStandardByBranchCodeIn(instituteBranchCodes);
            return standards.stream()
                    .map(this::mapToStandardDTO)
                    .collect(Collectors.toList());
        }

        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to get standard");
        }
        branchCodeToUse = staffService.fetchBranchCodeByRole(role, email);

        List<StudentStandard> standards = standardRepository.getAllStandardByBranchCode(branchCodeToUse);
        return standards.stream()
                .map(this::mapToStandardDTO)
                .collect(Collectors.toList());
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
