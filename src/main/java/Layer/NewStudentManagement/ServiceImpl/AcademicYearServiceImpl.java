package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentAcademicYear;
import Layer.NewStudentManagement.Enum.Role;
import Layer.NewStudentManagement.Repository.AcademicYearRepository;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.AcademicYearService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
public class AcademicYearServiceImpl implements AcademicYearService
{
    @Autowired
    private AcademicYearRepository academicYearRepository;

    @Autowired
    private StaffService staffService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public StudentAcademicYear createAcademicYear(String role, String email, StudentAcademicYear academicYear)
    {
        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create AcademicYear");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        academicYear.setBranchCode(branchCode);
        academicYear.setRole(Role.valueOf(role));
        academicYear.setCreatedByEmail(email);
        boolean exists = academicYearRepository.existsByAcademicYearAndBranchCode(
                academicYear.getAcademicYear(), branchCode);

        if (exists) {
            throw new IllegalArgumentException("Academic Year '" + academicYear.getAcademicYear() + "' already exists in this branch");
        }

        return academicYearRepository.save(academicYear);

    }

    @Override
    public StudentAcademicYear getAcademicYearById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to delete AcademicYear");
        }
        StudentAcademicYear academicYear = academicYearRepository.findById(id).orElseThrow(()->new RuntimeException("Year not found"));
        return academicYear;


    }

    @Override
    public StudentAcademicYear updateAcademicYear(Long id,String role,String email,StudentAcademicYear academicYear)
    {

        if(!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to update AcademicYear");
        }
        StudentAcademicYear existingAcademicYear = academicYearRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Stream not found"));
        existingAcademicYear.setAcademicYear(academicYear.getAcademicYear());
        return academicYearRepository.save(existingAcademicYear);

    }

    @Override
    public void deleteAcademicYearById(Long id,String role,String email)
    {
        if (!staffService.hasPermission(role,email,"Delete"))
            throw new RuntimeException("You don't have permission to delete AcademicYear");
        academicYearRepository.deleteById(id);
    }

    @Override
    public List<StudentAcademicYear> getAllAcademicYear(String role, String email,
                                                        @Nullable String branchCode, String token) {

        if ("USER".equalsIgnoreCase(role)) {
            Claims claims = jwtUtil.extractAllClaims(token);
            String encodedBranch = claims.get("branchCode", String.class);

            if (encodedBranch == null || encodedBranch.isEmpty()) {
                throw new RuntimeException("Invalid token: branchCode not found");
            }

            String decodedBranch = new String(Base64.getUrlDecoder().decode(encodedBranch), StandardCharsets.UTF_8);
            return academicYearRepository.findAllByBranchCode(decodedBranch);
        }

        if ("SUPERADMIN".equalsIgnoreCase(role)) {

            if (!staffService.hasPermission(role, email, "GET")) {
                throw new RuntimeException("You don't have permission to get AcademicYear");
            }

            List<String> instituteBranchCodes = staffService.getBranchCodesByInstituteEmail(email);
            if (instituteBranchCodes == null || instituteBranchCodes.isEmpty()) {
                throw new RuntimeException("No branches found for this institute email: " + email);
            }

            if (branchCode != null && !branchCode.isBlank()) {
                if (!instituteBranchCodes.contains(branchCode)) {
                    throw new RuntimeException("Filtered branchCode does not belong to your institute");
                }
                return academicYearRepository.findAllByBranchCode(branchCode);
            }

            try {
                return academicYearRepository.findAllByBranchCodeIn(instituteBranchCodes);
            } catch (Exception e) {
                List<StudentAcademicYear> result = new ArrayList<>();
                for (String code : instituteBranchCodes) {
                    result.addAll(academicYearRepository.findAllByBranchCode(code));
                }
                return result;
            }
        }

        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to get AcademicYear");
        }

        String resolvedBranch = staffService.fetchBranchCodeByRole(role, email);
        return academicYearRepository.findAllByBranchCode(resolvedBranch);
    }

}

