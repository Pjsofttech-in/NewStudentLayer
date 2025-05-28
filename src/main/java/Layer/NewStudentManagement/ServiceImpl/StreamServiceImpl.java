package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentStream;
import Layer.NewStudentManagement.Repository.StreamRepository;
import Layer.NewStudentManagement.Service.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public StudentStream getStreamById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get stream");
        }
        StudentStream stream = streamRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Stream not found"));
        return stream;
    }

    @Override
    public StudentStream updateStream(Long id,String role,String email,StudentStream stream)
    {
        if(!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to update stream");
        }
        StudentStream existingStream = streamRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Stream not found"));
        existingStream.setStream(stream.getStream());
        return streamRepository.save(existingStream);
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
    public List<StudentStream> getAllStream(String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get stream");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role,email);
        return streamRepository.findAllByBranchCode(branchCode);
    }
}
