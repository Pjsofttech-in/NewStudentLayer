package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentAcademicYear;
import Layer.NewStudentManagement.Entity.StudentEntity;

import Layer.NewStudentManagement.Repository.AcademicYearRepository;
import Layer.NewStudentManagement.Repository.StudentRepository;
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

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public StudentAcademicYear createAcademicYear(String role, String email, StudentAcademicYear academicYear)
    {
        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create AcademicYear");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        academicYear.setBranchCode(branchCode);
        academicYear.setRole(role);
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
    public List<StudentAcademicYear> getAllAcademicYear(String role, String email, String branchCode) {

        try {

            if ("STUDENT".equalsIgnoreCase(role) || "USER".equalsIgnoreCase(role) || "TEACHER".equalsIgnoreCase(role)) {

                // 🔥 FIX: auto-fetch branchCode
                if (branchCode == null || branchCode.isBlank()) {
                    branchCode = staffService.fetchBranchCodeByRole(role, email);
                }

                if (branchCode == null || branchCode.isBlank()) {
                    throw new RuntimeException("BranchCode not found");
                }

                return academicYearRepository.findAllByBranchCode(branchCode);
            }

            // ✅ SUPERADMIN
            if ("SUPERADMIN".equalsIgnoreCase(role)) {

                if (!staffService.hasPermission(role, email, "GET")) {
                    throw new RuntimeException("You don't have permission to get AcademicYear");
                }

                List<String> instituteBranchCodes =
                        staffService.getBranchCodesByInstituteEmail(email);

                if (instituteBranchCodes == null || instituteBranchCodes.isEmpty()) {
                    throw new RuntimeException("No branches found for this institute email: " + email);
                }

                return academicYearRepository.findAllByBranchCodeIn(instituteBranchCodes);
            }

            // ✅ OTHER ROLES (ADMIN / STAFF)
            if (!staffService.hasPermission(role, email, "GET")) {
                throw new RuntimeException("You don't have permission to get AcademicYear");
            }

            String resolvedBranch =
                    staffService.fetchBranchCodeByRole(role, email);

            if (resolvedBranch == null || resolvedBranch.isBlank()) {
                throw new RuntimeException("BranchCode not found for role: " + role);
            }

            return academicYearRepository.findAllByBranchCode(resolvedBranch);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
