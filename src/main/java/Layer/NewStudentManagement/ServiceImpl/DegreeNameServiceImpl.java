package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StudentDegreeNameDTO;
import Layer.NewStudentManagement.Entity.StudentDegreeName;
import Layer.NewStudentManagement.Entity.StudentGraduationType;
import Layer.NewStudentManagement.Repository.DegreeNameRepository;
import Layer.NewStudentManagement.Repository.GraduationTypeRepository;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.DegreeNameService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DegreeNameServiceImpl implements DegreeNameService
{

    @Autowired
    StaffService staffService;

    @Autowired
    DegreeNameRepository degreeNameRepository;

    @Autowired
    GraduationTypeRepository graduationTypeRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public StudentDegreeNameDTO saveDegreeName(String role, String email, StudentDegreeNameDTO request) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to create degreeName");
        }

        StudentGraduationType graduationType = graduationTypeRepository.findById(request.getGraduationTypeId())
                .orElseThrow(() -> new RuntimeException("GraduationType not found with ID: " + request.getGraduationTypeId()));

        StudentDegreeName degreeName = new StudentDegreeName();
        degreeName.setDegreeName(request.getDegreeName());
        degreeName.setGraduationType(graduationType);
        degreeName.setCreatedByEmail(email);
        degreeName.setRole(role);
        degreeName.setBranchCode(staffService.fetchBranchCodeByRole(role, email));

        StudentDegreeName saved = degreeNameRepository.save(degreeName);

        return mapToDegreeNameDTO(saved);
    }


    @Override
    public StudentDegreeNameDTO getDegreeNameById(Long id, String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get degreeName");
        }
        StudentDegreeName degreeName = degreeNameRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Division not found"));
        return mapToDegreeNameDTO(degreeName);
    }


    @Override
    public StudentDegreeNameDTO updateDegreeName(Long id,String role,String email,StudentDegreeName degreeName)
    {
        if(!staffService.hasPermission(role,email,"Put"))
    {
        throw new RuntimeException("You don't have permission to update degreeName");
    }
        StudentDegreeName existingDegreeName = degreeNameRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Division not found"));
        existingDegreeName.setDegreeName(degreeName.getDegreeName());
        StudentDegreeName saved = degreeNameRepository.save(existingDegreeName);

        return mapToDegreeNameDTO(saved);
    }

    @Override
    public void deleteDegreeNameById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Delete"))
        {
            throw new RuntimeException("You don't have permission to delete degreeName");
        }
        degreeNameRepository.deleteById(id);
    }

    @Override
    public List<StudentDegreeNameDTO> getAllDegreeName(String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get degreeName");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role,email);
        List<StudentDegreeName> degreeNames = degreeNameRepository.findAllByBranchCode(branchCode);
        return degreeNames.stream()
                .map(this::mapToDegreeNameDTO)
                .collect(Collectors.toList());

    }


    @Override
    public List<StudentDegreeNameDTO> getDegreeNamesByGraduationType(String role, String email, Long graduationTypeId, String token) {

        String branchCode;
        if ("USER".equalsIgnoreCase(role)) {

            Claims claims = jwtUtil.extractAllClaims(token);
            String encoded = claims.get("branchCode", String.class);

            if (encoded == null || encoded.isEmpty()) {
                throw new RuntimeException("Invalid token: branchCode not found");
            }

        } else {

            if (!staffService.hasPermission(role, email, "Get")) {
                throw new RuntimeException("You don't have permission to get degree names.");
            }
        }

        return degreeNameRepository.findByGraduationTypeId(graduationTypeId).stream()
                .map(this::mapToDegreeNameDTO)
                .collect(Collectors.toList());
    }

    private StudentDegreeNameDTO mapToDegreeNameDTO(StudentDegreeName entity) {
        StudentDegreeNameDTO dto = new StudentDegreeNameDTO();
        dto.setId(entity.getId());
        dto.setDegreeName(entity.getDegreeName());
        dto.setCreatedByEmail(entity.getCreatedByEmail());
        dto.setRole(entity.getRole());
        dto.setBranchCode(entity.getBranchCode());

        if (entity.getGraduationType() != null) {
            dto.setGraduationTypeId(entity.getGraduationType().getId());
            dto.setGraduationType(entity.getGraduationType().getGraduationType());
        }

        return dto;
    }



}
