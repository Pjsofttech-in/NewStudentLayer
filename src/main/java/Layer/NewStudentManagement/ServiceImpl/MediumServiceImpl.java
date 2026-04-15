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
import java.util.ArrayList;
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
    public List<MediumDTO> getAllMedium(String role, String email, String branchCode) {

        if ("STUDENT".equalsIgnoreCase(role) || "USER".equalsIgnoreCase(role) || "TEACHER".equalsIgnoreCase(role)) {


            if (branchCode == null || branchCode.isBlank()) {
                branchCode = staffService.fetchBranchCodeByRole(role, email);
            }

            if (branchCode == null || branchCode.isBlank()) {
                throw new RuntimeException("BranchCode not found for role: " + role);
            }

            return mediumRepository.findAllByBranchCode(branchCode)
                    .stream()
                    .map(this::mapToMediumDTO)
                    .collect(Collectors.toList());
        }

        // ✅ SUPERADMIN (KEEP SAME)
        if ("SUPERADMIN".equalsIgnoreCase(role)) {

            if (!staffService.hasPermission(role, email, "GET")) {
                throw new RuntimeException("You don't have permission to get Medium");
            }

            List<String> instituteBranchCodes = staffService.getBranchCodesByInstituteEmail(email);

            if (instituteBranchCodes == null || instituteBranchCodes.isEmpty()) {
                throw new RuntimeException("No branches found for this institute email: " + email);
            }

            return mediumRepository.findAllByBranchCodeIn(instituteBranchCodes)
                    .stream()
                    .map(this::mapToMediumDTO)
                    .collect(Collectors.toList());
        }

        // ✅ OTHER ROLES (ADMIN / STAFF etc.)
        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to get Medium");
        }

        // 🔥 SAME LOGIC AS getExams()
        String resolvedBranch = staffService.fetchBranchCodeByRole(role, email);

        if (resolvedBranch == null || resolvedBranch.isBlank()) {
            throw new RuntimeException("BranchCode not found for role: " + role);
        }

        return mediumRepository.findAllByBranchCode(resolvedBranch)
                .stream()
                .map(this::mapToMediumDTO)
                .collect(Collectors.toList());
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
