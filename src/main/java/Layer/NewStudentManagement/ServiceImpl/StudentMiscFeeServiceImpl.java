package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.MiscFeePaymentRequestDTO;
import Layer.NewStudentManagement.DTO.StudentMiscFeeDTO;
import Layer.NewStudentManagement.Entity.StudentFeeComponentsMaster;
import Layer.NewStudentManagement.Entity.StudentFees;
import Layer.NewStudentManagement.Entity.StudentFeesCollect;
import Layer.NewStudentManagement.Entity.StudentMiscFee;
import Layer.NewStudentManagement.Repository.FeeComponentMasterRepository;
import Layer.NewStudentManagement.Repository.FeesCollectRepository;
import Layer.NewStudentManagement.Repository.FeesRepository;
import Layer.NewStudentManagement.Repository.StudentMiscFeeRepository;
import Layer.NewStudentManagement.Service.FeesCollectService;
import Layer.NewStudentManagement.Service.StudentMiscFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static Layer.NewStudentManagement.Entity.StudentMiscFee.FeesStatus.*;

@Service
public class StudentMiscFeeServiceImpl implements StudentMiscFeeService {

    @Autowired
    private StudentMiscFeeRepository miscFeeRepository;

    @Autowired
    private FeesRepository studentFeesRepository;

    @Autowired
    private FeesCollectRepository feesCollectRepository;

    @Autowired
    private FeesCollectService feesCollectService;

    @Autowired
    private FeeComponentMasterRepository feeComponentRepository;

    @Autowired
    private StaffService staffService;

    @Override
    public StudentMiscFeeDTO assignMiscFee(String role, String email, StudentMiscFeeDTO request) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to assign miscellaneous fees.");
        }

        StudentFees studentFees = studentFeesRepository.findById(request.getStudentFeesId())
                .orElseThrow(() -> new RuntimeException("Student Academic Fees record not found with ID: " + request.getStudentFeesId()));

        StudentFeeComponentsMaster feeComponent = feeComponentRepository.findById(request.getFeeComponentId())
                .orElseThrow(() -> new RuntimeException("Fee Component not found with ID: " + request.getFeeComponentId()));

        StudentMiscFee miscFee = new StudentMiscFee();
        miscFee.setStudentFees(studentFees);
        miscFee.setFeeComponent(feeComponent);
        miscFee.setAmount(request.getAmount());
        miscFee.setDueDate(request.getDueDate());
        miscFee.setDescription(request.getDescription());

        // Defaults for a new fee
        miscFee.setPaidAmount(0.0);
        miscFee.setPendingAmount(request.getAmount());
        miscFee.setStatus(PENDING.name());
        miscFee.setAssignedDate(LocalDate.now());

        miscFee.setCreatedBy(email);
        miscFee.setCreatedByRole(role);
        miscFee.setBranchCode(staffService.fetchBranchCodeByRole(role, email));

        StudentMiscFee saved = miscFeeRepository.save(miscFee);
        return mapToDTO(saved);
    }

    @Override
    public StudentMiscFeeDTO updateMiscFee(Long id, String role, String email, StudentMiscFeeDTO request) {
        if (!staffService.hasPermission(role, email, "Put")) {
            throw new RuntimeException("You don't have permission to update miscellaneous fees.");
        }

        StudentMiscFee existingFee = miscFeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Misc Fee not found with ID: " + id));

        if (request.getAmount() != null) {
            // Validation: Prevent reducing the fee amount to less than what has already been paid
            if (request.getAmount() < existingFee.getPaidAmount()) {
                throw new RuntimeException("Cannot update fee amount to be less than the already paid amount (" + existingFee.getPaidAmount() + ").");
            }
            existingFee.setAmount(request.getAmount());
            existingFee.setPendingAmount(request.getAmount() - existingFee.getPaidAmount());
            // Auto-update status just in case they adjusted the amount to match exactly what is paid
            if (existingFee.getPaidAmount() >= existingFee.getAmount()) {
                existingFee.setStatus(PAID.name());
            } else if (existingFee.getPaidAmount() > 0) {
                existingFee.setStatus(PARTIALLY_PAID.name());
            } else {
                existingFee.setStatus(PENDING.name());
            }
        }
        existingFee.setDueDate(request.getDueDate());
        existingFee.setDescription(request.getDescription());

        existingFee.setUpdatedBy(email);
        existingFee.setUpdatedByRole(role);

        StudentMiscFee updated = miscFeeRepository.save(existingFee);
        return mapToDTO(updated);
    }

    // --- NEW: The Payment processing method ---
    @Override
    public StudentMiscFeeDTO payMiscFee(Long id, String role, String email, MiscFeePaymentRequestDTO request) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to collect fees.");
        }

        StudentMiscFee existingFee = miscFeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Misc Fee not found with ID: " + id));

        if (request.getAmountPaid() == null || request.getAmountPaid() <= 0) {
            throw new RuntimeException("Payment amount must be greater than zero.");
        }

        // Prevent overpayment
        if (request.getAmountPaid() > existingFee.getPendingAmount()) {
            throw new RuntimeException("Cannot pay more than the pending amount. Pending balance is: " + existingFee.getPendingAmount());
        }

        // 1. Create Fees Collect Record
        createFeeCollection(role, email, null, existingFee, request);

        // 2. Update the actual Misc Fee record balances
        double totalPaidAmount = existingFee.getPaidAmount() + request.getAmountPaid();
        double totalPendingAmount = existingFee.getAmount() - totalPaidAmount;
        existingFee.setPaidAmount(totalPaidAmount);
        existingFee.setPendingAmount(totalPendingAmount);

        // 3. Update Status dynamically
        if (existingFee.getPaidAmount() >= existingFee.getAmount()) {
            existingFee.setStatus(PAID.name());
        } else if (existingFee.getPaidAmount() >= 0) {
            existingFee.setStatus(PARTIALLY_PAID.name());
        } else {
            existingFee.setStatus(PENDING.name());
        }

        existingFee.setUpdatedByRole(role);
        existingFee.setUpdatedBy(email);

        StudentMiscFee updatedFee = miscFeeRepository.save(existingFee);
        return mapToDTO(updatedFee);
    }

    @Override
    public List<StudentMiscFeeDTO> getMiscFeesByStudentFeesId(Long studentFeesId, String role, String email) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to view fees.");
        }

        List<StudentMiscFee> miscFees = miscFeeRepository.findByStudentFeesFid(studentFeesId);

        return miscFees.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StudentMiscFeeDTO getMiscFeesById(Long miscFeesId, String role, String email) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to view fees.");
        }

        Optional<StudentMiscFee> miscFees = miscFeeRepository.findById(miscFeesId);

        return miscFees.map(this::mapToDTO).orElseThrow();
    }

    @Override
    public void deleteMiscFee(Long id, String role, String email) {
        if (!staffService.hasPermission(role, email, "Delete")) {
            throw new RuntimeException("You don't have permission to delete fees.");
        }

        StudentMiscFee existingFee = miscFeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Misc Fee not found with ID: " + id));

        // Strict Financial Rule: Cannot delete a fee if partial or full payment was already made
        if (existingFee.getPaidAmount() > 0) {
            throw new RuntimeException("Cannot delete this miscellaneous fee because a payment of " + existingFee.getPaidAmount() + " has already been collected. Please cancel the receipt first.");
        }

        miscFeeRepository.deleteById(id);
    }

    private StudentMiscFeeDTO mapToDTO(StudentMiscFee entity) {
        StudentMiscFeeDTO dto = new StudentMiscFeeDTO();
        dto.setId(entity.getId());
        dto.setAmount(entity.getAmount());
        dto.setPaidAmount(entity.getPaidAmount());
        dto.setPendingAmount(entity.getPendingAmount());
        dto.setDueDate(entity.getDueDate());
        dto.setStatus(entity.getStatus());
        dto.setDescription(entity.getDescription());
        dto.setCreatedByEmail(entity.getCreatedBy());
        dto.setBranchCode(entity.getBranchCode());
        dto.setAssignedDate(entity.getAssignedDate());

        if (entity.getStudentFees() != null) {
            dto.setStudentFeesId(entity.getStudentFees().getFid());
        }

        if (entity.getFeeComponent() != null) {
            dto.setFeeComponentId(entity.getFeeComponent().getId());
            dto.setComponentName(entity.getFeeComponent().getComponentName());
        }
        return dto;
    }

    public StudentFeesCollect createFeeCollection(String role, String email, String receiptId,
                                                  StudentMiscFee studentMiscFee,
                                                  MiscFeePaymentRequestDTO request) {
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        StudentFeesCollect sfc = new StudentFeesCollect();
        StudentFees studentFees = studentMiscFee.getStudentFees();
        sfc.setStudentMiscFee(studentMiscFee);
        sfc.setAmount(request.getAmountPaid());
        sfc.setMonth(LocalDate.now().getMonth().name());
        sfc.setFeesType("One Time");
        sfc.setDuedate(studentMiscFee.getDueDate());

        sfc.setIfscCode(request.getIfscCode());
        sfc.setBankBranchName(request.getBankBranchName());
        sfc.setBankName(request.getBankName());
        sfc.setAccountHolderName(request.getAccHolderName());

        sfc.setInvoice(feesCollectService.getInvoice());

        sfc.setTransactionId(receiptId);
        sfc.setFeesPaymentType("One Time");
        sfc.setPaymentDate(LocalDate.now());

        sfc.setCreatedByEmail(email);
        sfc.setRole(role);
        sfc.setBranchCode(branchCode);

        return feesCollectRepository.save(sfc);
    }
}