package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StudentGraduationTypeDTO;
import Layer.NewStudentManagement.Entity.StudentGraduationType;
import Layer.NewStudentManagement.Entity.StudentStream;
import Layer.NewStudentManagement.Repository.GraduationTypeRepository;
import Layer.NewStudentManagement.Repository.StreamRepository;
import Layer.NewStudentManagement.Security.JwtUtil;
import Layer.NewStudentManagement.Service.GraduationTypeService;
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
public class GraduationTypeServiceImpl implements GraduationTypeService
{

    @Autowired
    StaffService staffService;

    @Autowired
    GraduationTypeRepository graduationTypeRepository;

    @Autowired
    StreamRepository streamRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public StudentGraduationTypeDTO saveGraduationType(String role, String email, StudentGraduationTypeDTO request) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to create graduationType");
        }

        StudentStream stream = streamRepository.findById(request.getStreamId())
                .orElseThrow(() -> new RuntimeException("Stream not found with ID: " + request.getStreamId()));

        StudentGraduationType graduationType = new StudentGraduationType();
        graduationType.setGraduationType(request.getGraduationType());
        graduationType.setStream(stream);
        graduationType.setCreatedByEmail(email);
        graduationType.setRole(role);
        graduationType.setBranchCode(staffService.fetchBranchCodeByRole(role, email));


        StudentGraduationType saved = graduationTypeRepository.save(graduationType);
        return mapToGraduationTypeDTO(saved);
    }


    @Override
    public StudentGraduationTypeDTO getGraduationTypeById(Long id, String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get graduationType");
        }
        StudentGraduationType graduationType = graduationTypeRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Division not found"));
        return mapToGraduationTypeDTO(graduationType);
    }

    @Override
    public StudentGraduationTypeDTO updateGraduationType(Long id,String role,String email,StudentGraduationType graduationType)
    {
        if(!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to update graduationType");
        }
        StudentGraduationType existinggraduationType = graduationTypeRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Division not found"));
        existinggraduationType.setGraduationType(graduationType.getGraduationType());
        StudentGraduationType saved = graduationTypeRepository.save(existinggraduationType);
        return mapToGraduationTypeDTO(saved);


    }

    @Override
    public void deleteGraduationTypeById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Delete"))
        {
            throw new RuntimeException("You don't have permission to delete graduationType");
        }
        graduationTypeRepository.deleteById(id);

    }

    @Override
    public List<StudentGraduationTypeDTO> getAllGraduationType(String role, String email) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to get graduationType");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        if (branchCode == null || branchCode.trim().isEmpty()) {
            throw new RuntimeException("Branch code is missing for the given role/email.");
        }

        List<StudentGraduationType> graduationTypes = graduationTypeRepository.findAllByBranchCode(branchCode);

        if (graduationTypes == null || graduationTypes.isEmpty()) {
            return Collections.emptyList();
        }

        return graduationTypes.stream()
                .filter(Objects::nonNull)
                .map(this::mapToGraduationTypeDTO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    @Override
    public List<StudentGraduationTypeDTO> getGraduationTypesByStream(String role, String email, String streamName,
                                                                     String branchCode, String token) {
        String effectiveBranchCode = null;
        List<StudentGraduationTypeDTO> result;

        if (role == null) {
            throw new RuntimeException("Role is required");
        }

        String roleUpper = role.toUpperCase();

        switch (roleUpper) {

            case "USER": {
                if (token == null || token.isEmpty()) {
                    throw new RuntimeException("Token is required for USER role");
                }
                Claims claims = jwtUtil.extractAllClaims(token);
                String encoded = claims.get("branchCode", String.class);

                if (encoded == null || encoded.isEmpty()) {
                    throw new RuntimeException("Invalid token: branchCode not found");
                }

                effectiveBranchCode = new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
                if (branchCode != null && !branchCode.isEmpty()) {
                    effectiveBranchCode = branchCode;
                }

                result = graduationTypeRepository.findByStreamName(streamName, effectiveBranchCode)
                        .stream()
                        .map(this::mapToGraduationTypeDTO)
                        .collect(Collectors.toList());
                return result;
            }

            case "SUPERADMIN": {
                if (branchCode != null && !branchCode.isEmpty()) {
                    effectiveBranchCode = branchCode;
                    result = graduationTypeRepository.findByStreamName(streamName, effectiveBranchCode)
                            .stream()
                            .map(this::mapToGraduationTypeDTO)
                            .collect(Collectors.toList());
                    return result;
                }

                boolean hasPerm = staffService.hasPermission(role, email, "GET");
                if (!hasPerm) {
                    throw new RuntimeException("You don't have permission or institute email not found for SUPERADMIN.");
                }

                List<String> branchCodes = staffService.getBranchCodesByInstituteEmail(email);
                if (branchCodes == null || branchCodes.isEmpty()) {
                    return Collections.emptyList();
                }

                result = graduationTypeRepository.findByStreamNameAndBranchCodeIn(streamName, branchCodes)
                        .stream()
                        .map(this::mapToGraduationTypeDTO)
                        .collect(Collectors.toList());
                return result;
            }

            default: {
                if (!staffService.hasPermission(role, email, "Get")) {      // note: original used "Get"
                    throw new RuntimeException("You don't have permission to get graduation types.");
                }

                effectiveBranchCode = staffService.fetchBranchCodeByRole(role, email);

                if (branchCode != null && !branchCode.isEmpty()) {
                    effectiveBranchCode = branchCode;
                }

                result = graduationTypeRepository.findByStreamName(streamName, effectiveBranchCode)
                        .stream()
                        .map(this::mapToGraduationTypeDTO)
                        .collect(Collectors.toList());
                return result;
            }
        }
    }

    private StudentGraduationTypeDTO mapToGraduationTypeDTO(StudentGraduationType entity) {
        StudentGraduationTypeDTO dto = new StudentGraduationTypeDTO();
        dto.setId(entity.getId());
        dto.setGraduationType(entity.getGraduationType());

        if (entity.getStream() != null) {
            dto.setStreamId(entity.getStream().getId());
            dto.setStreamName(entity.getStream().getStream()); // Optional, if you include stream name
        }

        dto.setCreatedByEmail(entity.getCreatedByEmail());
        dto.setRole(entity.getRole());
        dto.setBranchCode(entity.getBranchCode());

        return dto;
    }


}
