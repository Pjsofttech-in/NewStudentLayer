package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentAcademicYear;
import Layer.NewStudentManagement.Repository.AcademicYearRepository;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.AcademicYearService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
        academicYear.setRole(role);
        academicYear.setCreatedByEmail(email);
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
    public List<StudentAcademicYear> getAllAcademicYear(String role, String email, String token) {
        String branchCode;
        if ("USER".equalsIgnoreCase(role)) {
            Claims claims = jwtUtil.extractAllClaims(token);
            String encoded = claims.get("branchCode", String.class);

            if (encoded == null || encoded.isEmpty()) {
                throw new RuntimeException("Invalid token: branchCode not found");
            }

            branchCode = new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
        } else {
            if (!staffService.hasPermission(role, email, "Get")) {
                throw new RuntimeException("You don't have permission to get AcademicYear");
            }
            branchCode = staffService.fetchBranchCodeByRole(role, email);
        }

        return academicYearRepository.findAllByBranchCode(branchCode);

    }
}

