package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.BonafideRequestDTO;
import java.util.List;

public interface BonafideRequestService {

    // --- STUDENT ACTIONS ---
    BonafideRequestDTO raiseRequest(Long studentId, String reason, String role, String email);
    List<BonafideRequestDTO> getMyRequests(Long studentId, String role, String email);

    // --- STAFF ACTIONS ---
    List<BonafideRequestDTO> getPendingRequestsForBranch(String role, String email);
    BonafideRequestDTO processRequest(Long requestId, String status, String remarks, String role, String email);
}