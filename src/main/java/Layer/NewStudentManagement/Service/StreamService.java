package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.StreamDTO;
import Layer.NewStudentManagement.Entity.StudentStream;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public interface StreamService
{
    StudentStream createStream(String role,String email,StudentStream stream);
    StreamDTO getStreamById(Long id, String role, String email);
    StreamDTO updateStream(Long id,String role,String email,StudentStream stream);
    void deleteStreamById(Long id,String role,String email);
    List<StreamDTO> getAllStream(String role, String email, @Nullable String branchCode, String token);
}
