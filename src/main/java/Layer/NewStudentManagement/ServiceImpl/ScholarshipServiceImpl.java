package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentScholarship;

import Layer.NewStudentManagement.Repository.ScholarshipRepository;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.ScholarshipService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScholarshipServiceImpl implements ScholarshipService
{

    @Autowired
    ScholarshipRepository scholarshipRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    StaffService staffService;

    
    @Override
    public StudentScholarship createScholarship(String role, String email, StudentScholarship scholarship)
    {

        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create scholarship");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role,email);

        if(scholarshipRepository.existsScholarship(
                scholarship.getScholarshipName(),branchCode))
        {
            throw new RuntimeException("Scholarship already exists for this branch");
        }

        scholarship.setBranchCode(branchCode);
        scholarship.setRole(role);
        scholarship.setCreatedByEmail(email);

        return scholarshipRepository.save(scholarship);
    }

    @Override
    public StudentScholarship getScholarshipById(Long id, String role, String email)
    {

        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get scholarship");
        }

        StudentScholarship scholarship = scholarshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scholarship not found"));

        return scholarship;
    }

    @Override
    public List<StudentScholarship> getAllScholarships(String role, String email,
                                                       @Nullable String branchCode,
                                                       String token) {

        // USER ROLE
        if ("USER".equalsIgnoreCase(role)) {

            Claims claims = jwtUtil.extractAllClaims(token);
            String encodedBranch = claims.get("branchCode", String.class);

            if (encodedBranch == null || encodedBranch.isEmpty()) {
                throw new RuntimeException("Invalid token: branchCode not found");
            }

            String decodedBranch = new String(
                    Base64.getUrlDecoder().decode(encodedBranch),
                    StandardCharsets.UTF_8
            );

            return scholarshipRepository.findAllByBranchCode(decodedBranch);
        }

        // SUPERADMIN ROLE
        if ("SUPERADMIN".equalsIgnoreCase(role)) {

            if (!staffService.hasPermission(role, email, "GET")) {
                throw new RuntimeException("You don't have permission to get scholarships");
            }

            List<String> instituteBranchCodes = staffService.getBranchCodesByInstituteEmail(email);

            if (instituteBranchCodes == null || instituteBranchCodes.isEmpty()) {
                throw new RuntimeException("No branches found for this institute email: " + email);
            }

            // If branchCode filter provided
            if (branchCode != null && !branchCode.isBlank()) {

                if (!instituteBranchCodes.contains(branchCode)) {
                    throw new RuntimeException("Filtered branchCode does not belong to your institute");
                }

                return scholarshipRepository.findAllByBranchCode(branchCode);
            }

            try {
                return scholarshipRepository.findAllByBranchCodeIn(instituteBranchCodes);
            }
            catch (Exception e) {

                List<StudentScholarship> result = new ArrayList<>();

                for (String code : instituteBranchCodes) {
                    result.addAll(scholarshipRepository.findAllByBranchCode(code));
                }

                return result;
            }
        }

        // OTHER ROLES (ADMIN / STAFF)
        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to get scholarships");
        }

        String resolvedBranch = staffService.fetchBranchCodeByRole(role, email);

        return scholarshipRepository.findAllByBranchCode(resolvedBranch);
    }
    @Override
    public StudentScholarship updateScholarship(Long id, String role, String email, StudentScholarship scholarship)
    {

        if(!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to update scholarship");
        }

        StudentScholarship existing = scholarshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scholarship not found"));

        existing.setScholarshipName(scholarship.getScholarshipName());

        return scholarshipRepository.save(existing);
    }

    @Override
    public void deleteScholarship(Long id, String role, String email)
    {

        if(!staffService.hasPermission(role,email,"Delete"))
        {
            throw new RuntimeException("You don't have permission to delete scholarship");
        }

        StudentScholarship scholarship = scholarshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scholarship not found"));

        scholarshipRepository.delete(scholarship);
    }


}
