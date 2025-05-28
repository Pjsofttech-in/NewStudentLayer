package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.Entity.StudentStream;

import java.util.List;

public interface StreamService
{
    StudentStream createStream(String role,String email,StudentStream stream);
    StudentStream getStreamById(Long id,String role,String email);
    StudentStream updateStream(Long id,String role,String email,StudentStream stream);
    void deleteStreamById(Long id,String role,String email);
    List<StudentStream> getAllStream(String role, String email);
}
