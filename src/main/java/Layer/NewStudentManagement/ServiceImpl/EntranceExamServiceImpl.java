package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentEntranceExam;

import Layer.NewStudentManagement.Repository.EntranceExamRepository;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.EntranceExamService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class EntranceExamServiceImpl implements EntranceExamService
{

    @Autowired
    EntranceExamRepository entranceExamRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    StaffService staffService;

    @Override
    public StudentEntranceExam createIntranceExam(String role, String email,StudentEntranceExam exam)
    {

        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create IntranceExam");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role,email);

        if(entranceExamRepository.existsIntranceName(
                exam.getEntranceExamName(),branchCode))
        {
            throw new RuntimeException("Intrance Exam already exists for this branch");
        }

        exam.setBranchCode(branchCode);
        exam.setRole(role);
        exam.setCreatedByEmail(email);

        return entranceExamRepository.save(exam);
    }

    @Override
    public List<StudentEntranceExam> getAllIntranceExams(String role, String email,
                                                         @Nullable String branchCode, String token) {

        // USER ROLE
        if ("USER".equalsIgnoreCase(role)) {

            Claims claims = jwtUtil.extractAllClaims(token);
            String encodedBranch = claims.get("branchCode", String.class);

            if (encodedBranch == null || encodedBranch.isEmpty()) {
                throw new RuntimeException("Invalid token: branchCode not found");
            }

            String decodedBranch = new String(Base64.getUrlDecoder().decode(encodedBranch), StandardCharsets.UTF_8);

            return entranceExamRepository.findAllByBranchCode(decodedBranch);
        }

        // SUPERADMIN ROLE
        if ("SUPERADMIN".equalsIgnoreCase(role)) {

            if (!staffService.hasPermission(role, email, "GET")) {
                throw new RuntimeException("You don't have permission to get EntranceExam");
            }

            List<String> instituteBranchCodes = staffService.getBranchCodesByInstituteEmail(email);

            if (instituteBranchCodes == null || instituteBranchCodes.isEmpty()) {
                throw new RuntimeException("No branches found for this institute email: " + email);
            }

            // If branch filter provided
            if (branchCode != null && !branchCode.isBlank()) {

                if (!instituteBranchCodes.contains(branchCode)) {
                    throw new RuntimeException("Filtered branchCode does not belong to your institute");
                }

                return entranceExamRepository.findAllByBranchCode(branchCode);
            }

            // Fetch all branches
            try {
                return entranceExamRepository.findAllByBranchCodeIn(instituteBranchCodes);
            } catch (Exception e) {

                List<StudentEntranceExam> result = new ArrayList<>();

                for (String code : instituteBranchCodes) {
                    result.addAll(entranceExamRepository.findAllByBranchCode(code));
                }

                return result;
            }
        }

        // OTHER ROLES (ADMIN / STAFF)
        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to get EntranceExam");
        }

        String resolvedBranch = staffService.fetchBranchCodeByRole(role, email);

        return entranceExamRepository.findAllByBranchCode(resolvedBranch);
    }

    @Override
    public StudentEntranceExam getIntranceExamById(String role, String email,Long id)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to Get IntranceExam");
        }

        StudentEntranceExam entranceExam = entranceExamRepository.findById(id).orElseThrow(()->
                new RuntimeException("Entrance exam Not Found"));

        return entranceExam;


    }

    @Override
    public StudentEntranceExam updateIntranceExam(Long id, String role, String email,StudentEntranceExam exam)
    {
        if(!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to Get IntranceExam");
        }

        StudentEntranceExam entranceExam = entranceExamRepository.findById(id).orElseThrow(()->
                new RuntimeException("Entrance exam Not Found"));
        entranceExam.setEntranceExamName(exam.getEntranceExamName());
        return entranceExam;

    }

    @Override
    public String deleteIntranceExam(Long id,String role, String email)
    {

        if(!staffService.hasPermission(role,email,"Delete"))
        {
            throw new RuntimeException("You don't have permission to Get IntranceExam");
        }

        StudentEntranceExam entranceExam = entranceExamRepository.findById(id).orElseThrow(()->
                new RuntimeException("Entrance exam Not Found"));

         entranceExamRepository.deleteById(id);
        return "Exam deleted successfully";

    }


}
