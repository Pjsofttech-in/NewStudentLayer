package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.Entity.StudentDuplicateTCRequest;

import java.util.List;

public interface DuplicteTCRequestService
{
    StudentDuplicateTCRequest createDuplicateTCRequest(Long studentId,
                                                       StudentDuplicateTCRequest request,
                                                       String role, String email);

    StudentDuplicateTCRequest approveDuplicateTCRequest(Long requestId,String role, String email);

    List<StudentDuplicateTCRequest> getAllDuplicateTCRequests(String role, String email);

}
