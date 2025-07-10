package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StudentDepartmentDTO;
import Layer.NewStudentManagement.DTO.StudentGroupDTO;
import Layer.NewStudentManagement.Entity.StudentDepartment;
import Layer.NewStudentManagement.Entity.StudentGraduationType;
import Layer.NewStudentManagement.Entity.StudentGroup;
import Layer.NewStudentManagement.Repository.GraduationTypeRepository;
import Layer.NewStudentManagement.Repository.GroupRepository;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.GroupService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService
{
    @Autowired
    private StaffService staffService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GraduationTypeRepository graduationTypeRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public StudentGroupDTO createGroup(String role, String email, StudentGroup group)
    {
        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create group");
        }

        StudentGraduationType graduationType = graduationTypeRepository.findById(group.getGraduationType().getId())
                .orElseThrow(() -> new RuntimeException("Graduation type not found"));

        if ("Jr.College".equalsIgnoreCase(graduationType.getGraduationType())) {
            group.setGraduationType(graduationType);
        } else {
            group.setGraduationType(null);
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        group.setRole(role);
        group.setBranchCode(branchCode);
        group.setGraduationTypeName(graduationType.getGraduationType());
        group.setCreatedByEmail(email);
        StudentGroup group1 = groupRepository.save(group);
        return mapToGroupDTO(group1);

    }

    @Override
    public StudentGroupDTO getGroupById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get group");
        }
        StudentGroup group = groupRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Group not found"));
        return mapToGroupDTO(group);

    }

    @Override
    public StudentGroupDTO updateGroup(Long id,String role,String email,StudentGroup group)
    {
        if (!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to update group");
        }
        StudentGroup existingGroup = groupRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Group not found"));
        existingGroup.setStudentGroup(group.getStudentGroup());
        StudentGroup group1 = groupRepository.save(existingGroup);
        return mapToGroupDTO(group1);
    }

    @Override
    public void deleteGroupById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Delete"))
        {
            throw new RuntimeException("You don't have permission to delete group");
        }
        groupRepository.deleteById(id);
    }

    @Override
    public List<StudentGroupDTO> getAllGroupByName(String role, String email,String token) {
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
                throw new RuntimeException("You don't have permission to get group");
            }

            branchCode = staffService.fetchBranchCodeByRole(role, email);
        }

        if (branchCode == null || branchCode.trim().isEmpty()) {
            throw new RuntimeException("Branch code not found for given role and email.");
        }

        List<StudentGroup> groups = groupRepository.getAllByBranchCode(branchCode);

        if (groups == null || groups.isEmpty()) {
            return Collections.emptyList();
        }

        return groups.stream()
                .filter(Objects::nonNull)
                .map(this::mapToGroupDTO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    @Override
    public List<StudentGroupDTO> getGroupsByGraduationTypeId(String role, String email,Long graduationTypeId)
    {
        if (!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get group");
        }
        List<StudentGroup> groups = groupRepository.findGroupsByGraduationTypeId(graduationTypeId);
        return groups.stream()
                .map(this::mapToGroupDTO)
                .collect(Collectors.toList());
    }


    private StudentGroupDTO mapToGroupDTO(StudentGroup group) {
        StudentGroupDTO dto = new StudentGroupDTO();
        dto.setId(group.getId());
        dto.setStudentGroup(group.getStudentGroup());
        dto.setGraduationTypeName(group.getGraduationTypeName());
        if (group.getGraduationType() != null && group.getGraduationType().getGraduationType() != null) {
            dto.setGraduationTypeId(group.getGraduationType().getId());
        } else {
            dto.setGraduationTypeId(0L); // or null if preferred
        }
        dto.setCreatedByEmail(group.getCreatedByEmail());
        dto.setRole(group.getRole());
        dto.setBranchCode(group.getBranchCode());

        return dto;
    }
}
