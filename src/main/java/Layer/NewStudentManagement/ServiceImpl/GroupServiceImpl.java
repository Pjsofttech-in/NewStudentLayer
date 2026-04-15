package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StudentGroupDTO;
//import Layer.NewStudentManagement.Entity.StudentDepartment;
import Layer.NewStudentManagement.Entity.StudentGraduationType;
import Layer.NewStudentManagement.Entity.StudentGroup;

import Layer.NewStudentManagement.Repository.GraduationTypeRepository;
import Layer.NewStudentManagement.Repository.GroupRepository;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.GroupService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
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

        if ("Jr. College".equalsIgnoreCase(graduationType.getGraduationType())) {
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
    public List<StudentGroupDTO> getAllGroup(String role, String email, String branchCode) {

        try {

            if ("STUDENT".equalsIgnoreCase(role) || "USER".equalsIgnoreCase(role) || "TEACHER".equalsIgnoreCase(role)) {

                // 🔥 FIX: auto-fetch branchCode
                if (branchCode == null || branchCode.isBlank()) {
                    branchCode = staffService.fetchBranchCodeByRole(role, email);
                }

                if (branchCode == null || branchCode.isBlank()) {
                    throw new RuntimeException("BranchCode not found");
                }

                return groupRepository.getAllByBranchCode(branchCode).stream()
                        .map(this::mapToGroupDTO)
                        .collect(Collectors.toList());
            }

            // ✅ SUPERADMIN
            if ("SUPERADMIN".equalsIgnoreCase(role)) {

                if (!staffService.hasPermission(role, email, "GET")) {
                    throw new RuntimeException("You don't have permission to get Group");
                }

                List<String> instituteBranchCodes =
                        staffService.getBranchCodesByInstituteEmail(email);

                if (instituteBranchCodes == null || instituteBranchCodes.isEmpty()) {
                    throw new RuntimeException("No branches found for this institute email: " + email);
                }

                return groupRepository.findAllByBranchCodeIn(instituteBranchCodes).stream()
                        .map(this::mapToGroupDTO)
                        .collect(Collectors.toList());
            }

            // ✅ OTHER ROLES (ADMIN / STAFF)
            if (!staffService.hasPermission(role, email, "GET")) {
                throw new RuntimeException("You don't have permission to get Group");
            }

            String resolvedBranch =
                    staffService.fetchBranchCodeByRole(role, email);

            if (resolvedBranch == null || resolvedBranch.isBlank()) {
                throw new RuntimeException("BranchCode not found for role: " + role);
            }

            return groupRepository.getAllByBranchCode(resolvedBranch).stream()
                    .map(this::mapToGroupDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
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
