package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StudentGraduationTypeDTO;
import Layer.NewStudentManagement.Entity.StudentGraduationType;
import Layer.NewStudentManagement.Entity.StudentStream;
import Layer.NewStudentManagement.Repository.GraduationTypeRepository;
import Layer.NewStudentManagement.Repository.StreamRepository;
import Layer.NewStudentManagement.Service.GraduationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<StudentGraduationTypeDTO> getGraduationTypesByStream(String role, String email, String streamName) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to get graduation types.");
        }

       String branchCode =  staffService.fetchBranchCodeByRole(role, email);

        return graduationTypeRepository.findByStreamName(streamName,branchCode).stream()
                .map(this::mapToGraduationTypeDTO)
                .collect(Collectors.toList());
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
