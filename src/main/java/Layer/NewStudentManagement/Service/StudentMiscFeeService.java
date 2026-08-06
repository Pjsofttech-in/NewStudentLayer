package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.MiscFeePaymentRequestDTO;
import Layer.NewStudentManagement.DTO.StudentMiscFeeDTO;
import java.util.List;

public interface StudentMiscFeeService {

    StudentMiscFeeDTO assignMiscFee(String role, String email, StudentMiscFeeDTO request);

    StudentMiscFeeDTO updateMiscFee(Long id, String role, String email, StudentMiscFeeDTO request);

    // --- NEW: Dedicated method to handle payments and receipt generation ---
    StudentMiscFeeDTO payMiscFee(Long id, String role, String email, MiscFeePaymentRequestDTO request);

    List<StudentMiscFeeDTO> getMiscFeesByStudentFeesId(Long studentFeesId, String role, String email);

    StudentMiscFeeDTO getMiscFeesById(Long miscFeesId, String role, String email);

    void deleteMiscFee(Long id, String role, String email);
}