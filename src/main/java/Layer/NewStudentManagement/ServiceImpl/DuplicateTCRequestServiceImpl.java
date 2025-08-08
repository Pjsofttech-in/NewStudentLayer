package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentDuplicateTCRequest;
import Layer.NewStudentManagement.Entity.StudentEntity;
import Layer.NewStudentManagement.Entity.StudentTcData;
import Layer.NewStudentManagement.Repository.DuplicateTCRequestRepository;
import Layer.NewStudentManagement.Repository.StudentRepository;
import Layer.NewStudentManagement.Repository.TCDataRepository;
import Layer.NewStudentManagement.Service.DuplicteTCRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DuplicateTCRequestServiceImpl implements DuplicteTCRequestService
{
    @Autowired
    DuplicateTCRequestRepository duplicateTCRequestRepository;

    @Autowired
    StaffService staffService;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TCDataRepository tcDataRepository;

    @Override
    public StudentDuplicateTCRequest createDuplicateTCRequest(Long studentId,
                                                              StudentDuplicateTCRequest request,
                                                              String role, String email) {

        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to request duplicate TC");
        }
        List<StudentTcData> tcData = tcDataRepository.findAllByStudentId(studentId);
        if (tcData.isEmpty()) {
            throw new RuntimeException("No TC record found for this student to duplicate");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        request.setStudentId(studentId);
        request.setTcNumber(tcData.get(0).getTcNumber());
        request.setRequestDate(LocalDate.now());
        request.setStatus("PENDING");
        request.setReason(request.getReason());
        request.setCreatedByEmail(email);
        request.setRole(role);
        request.setBranchCode(branchCode);

        return duplicateTCRequestRepository.save(request);
    }

    @Override
    public StudentDuplicateTCRequest approveDuplicateTCRequest(Long requestId,String role, String email) {

        if (!staffService.hasPermission(role, email, "Put")) {
            throw new RuntimeException("You don't have permission to Approved duplicate TC Request");
        }
        StudentDuplicateTCRequest request = duplicateTCRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Duplicate TC Request not found"));

        request.setStatus("Approved");

        StudentEntity student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        student.setTcGenrated(true);

        studentRepository.save(student);
        return duplicateTCRequestRepository.save(request);
    }

    @Override
    public List<StudentDuplicateTCRequest> getAllDuplicateTCRequests(String role, String email)
    {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Get duplicate TC Request");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        return duplicateTCRequestRepository.findAllTCRequestByBranchCode(branchCode);
    }


}
