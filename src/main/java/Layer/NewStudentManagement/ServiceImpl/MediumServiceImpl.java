package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.MediumDTO;
import Layer.NewStudentManagement.Entity.StudentMedium;

import Layer.NewStudentManagement.Repository.MediumRepository;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.MediumService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MediumServiceImpl implements MediumService
{

    @Autowired
    private StaffService staffService;

    @Autowired
    private MediumRepository mediumRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public StudentMedium createMedium(String role, String email, StudentMedium medium)
    {
        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create medium");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        if (mediumRepository.existsByMediumNameIgnoreCaseAndBranchCode(medium.getMediumName(), branchCode)) {
            throw new RuntimeException("Medium already exists for this branch");
        }
        medium.setBranchCode(branchCode);
        medium.setRole(role);
        medium.setCreatedByEmail(email);
        return mediumRepository.save(medium);
    }

    @Override
    public MediumDTO getMediumById(Long id, String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get medium");
        }
        StudentMedium medium = mediumRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Medium not found"));
        return mapToMediumDTO(medium);
    }

    @Override
    public StudentMedium updateMedium(Long id,String role,String email,StudentMedium medium)
    {
        if(!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to update medium");
        }
        StudentMedium existingMedium = mediumRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Medium not found"));
        existingMedium.setMediumName(medium.getMediumName());
        return mediumRepository.save(existingMedium);
    }

    @Override
    public void deleteMediumById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Delete"))
        {
            throw new RuntimeException("You don't have permission to delete medium");
        }
        mediumRepository.deleteById(id);
    }

    @Override
    public List<MediumDTO> getAllMedium(String role, String email, String token, String branchCode)
    {
        String branchCodeToUse;

        if ("USER".equalsIgnoreCase(role)) {
            Claims claims = jwtUtil.extractAllClaims(token);
            String encoded = claims.get("branchCode", String.class);

            if (encoded == null || encoded.isEmpty()) {
                throw new RuntimeException("Invalid token: branchCode not found");
            }

            branchCodeToUse = new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
            return mediumRepository.findAllByBranchCode(branchCodeToUse).stream()
                    .map(this::mapToMediumDTO)
                    .collect(Collectors.toList());

        } else if ("SUPERADMIN".equalsIgnoreCase(role)) {
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

            // fetch all mediums for the branch codes list
            return mediumRepository.findAllByBranchCodeIn(instituteBranchCodes).stream()
                    .map(this::mapToMediumDTO)
                    .collect(Collectors.toList());

        } else {
            if (!staffService.hasPermission(role, email, "Get")) {
                throw new RuntimeException("You don't have permission to view Industry");
            }
            branchCodeToUse = staffService.fetchBranchCodeByRole(role, email);
            return mediumRepository.findAllByBranchCode(branchCodeToUse).stream()
                    .map(this::mapToMediumDTO)
                    .collect(Collectors.toList());
        }
    }


    private MediumDTO mapToMediumDTO(StudentMedium medium) {
        return new MediumDTO(
                medium.getMid(),
                medium.getMediumName(),
                medium.getCreatedByEmail(),
                medium.getRole(),
                medium.getBranchCode()
        );
    }

}
