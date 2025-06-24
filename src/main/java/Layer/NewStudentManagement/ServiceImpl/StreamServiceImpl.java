package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StreamDTO;
import Layer.NewStudentManagement.DTO.StudentDivisionDTO;
import Layer.NewStudentManagement.Entity.StudentDivision;
import Layer.NewStudentManagement.Entity.StudentStream;
import Layer.NewStudentManagement.Repository.StreamRepository;
import Layer.NewStudentManagement.Service.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StreamServiceImpl implements StreamService
{
    @Autowired
    private StaffService staffService;

    @Autowired
    private StreamRepository streamRepository;

    @Override
    public StudentStream createStream(String role, String email, StudentStream stream)
    {
        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create stream");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        stream.setBranchCode(branchCode);
        stream.setRole(role);
        stream.setCreatedByEmail(email);
        return streamRepository.save(stream);

    }

    @Override
    public StreamDTO getStreamById(Long id, String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get stream");
        }
        StudentStream stream = streamRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Stream not found"));
        return mapToStreamDTO(stream);
    }

    @Override
    public StreamDTO updateStream(Long id,String role,String email,StudentStream stream)
    {
        if(!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to update stream");
        }
        StudentStream existingStream = streamRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Stream not found"));
        existingStream.setStream(stream.getStream());
        StudentStream saved = streamRepository.save(existingStream);
        return mapToStreamDTO(saved);
    }

    @Override
    public void deleteStreamById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Delete"))
        {
            throw new RuntimeException("You don't have permission to delete stream");
        }
        streamRepository.deleteById(id);
    }

    @Override
    public List<StreamDTO> getAllStream(String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get stream");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role,email);
        List<StudentStream> saved = streamRepository.findAllByBranchCode(branchCode);
        return saved.stream()
                .map(this::mapToStreamDTO)
                .collect(Collectors.toList());
    }

    private StreamDTO mapToStreamDTO(StudentStream stream) {
        return new StreamDTO(
                stream.getId(),
                stream.getStream(),
                stream.getCreatedByEmail(),
                stream.getRole(),
                stream.getBranchCode()
        );
    }
}
