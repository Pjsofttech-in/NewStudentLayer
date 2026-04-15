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
    public List<StudentScholarship> getAllScholarships(String role, String email, String branchCode) {

        try {

            if ("STUDENT".equalsIgnoreCase(role) || "USER".equalsIgnoreCase(role) || "TEACHER".equalsIgnoreCase(role)) {

                // 🔥 FIX: auto-fetch branchCode
                if (branchCode == null || branchCode.isBlank()) {
                    branchCode = staffService.fetchBranchCodeByRole(role, email);
                }

                if (branchCode == null || branchCode.isBlank()) {
                    throw new RuntimeException("BranchCode not found");
                }

                return scholarshipRepository.findAllByBranchCode(branchCode);
            }

            // ✅ SUPERADMIN
            if ("SUPERADMIN".equalsIgnoreCase(role)) {

                if (!staffService.hasPermission(role, email, "GET")) {
                    throw new RuntimeException("You don't have permission to get Scholarship");
                }

                List<String> instituteBranchCodes =
                        staffService.getBranchCodesByInstituteEmail(email);

                if (instituteBranchCodes == null || instituteBranchCodes.isEmpty()) {
                    throw new RuntimeException("No branches found for this institute email: " + email);
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

            // ✅ OTHER ROLES (ADMIN / STAFF)
            if (!staffService.hasPermission(role, email, "GET")) {
                throw new RuntimeException("You don't have permission to get Scholarship");
            }

            String resolvedBranch =
                    staffService.fetchBranchCodeByRole(role, email);

            if (resolvedBranch == null || resolvedBranch.isBlank()) {
                throw new RuntimeException("BranchCode not found for role: " + role);
            }

            return scholarshipRepository.findAllByBranchCode(resolvedBranch);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
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
