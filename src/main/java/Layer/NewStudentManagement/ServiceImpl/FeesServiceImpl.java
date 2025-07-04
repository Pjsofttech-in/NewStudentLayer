package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StudentFeesDTO;
import Layer.NewStudentManagement.Entity.StudentEntity;
import Layer.NewStudentManagement.Entity.StudentFees;
import Layer.NewStudentManagement.Entity.StudentStandard;
import Layer.NewStudentManagement.Repository.FeesRepository;
import Layer.NewStudentManagement.Repository.StandardRepository;
import Layer.NewStudentManagement.Repository.StudentRepository;
import Layer.NewStudentManagement.Service.FeesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeesServiceImpl implements FeesService
{
    @Autowired
    private FeesRepository feesRepository;

    @Autowired
    private StaffService staffService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StandardRepository standardRepository;


    private void checkPermission(String role, String email, String action) {
        if (!staffService.hasPermission(role, email, action)) {
            throw new RuntimeException("You don't have permission to " + action.toLowerCase() + " Fees");
        }
    }

    @Override
    public StudentFeesDTO assignFeesToStudent(String role, String email, StudentFees fees)
    {
        checkPermission(role,email,"Post");

        StudentEntity student = studentRepository.findById(fees.getStudent().getId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        StudentStandard standard = standardRepository.findById(fees.getStandard().getSid())
                .orElseThrow(() -> new RuntimeException("Standard not found"));

        if (feesRepository.existsByStudentAndStandard(student, standard)) {
            throw new RuntimeException("Fees already assigned for this student and standard.");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role,email);

        fees.setDiscountedAmount(fees.getDiscountedAmount());
        fees.setTotalamount(fees.getTotalamount());
        fees.setStudent(student);
        fees.setStudentName(student.getFullName());
        fees.setMediumName(student.getMediumName());
        fees.setApprovalDate(student.getApprovalDate());
        fees.setRollNo(student.getRollNo());
        fees.setStandard(standard);
        fees.setStandardName(standard.getStandardName());
        fees.setPendingAmount(fees.getTotalamount());
        fees.setCreatedByEmail(email);
        fees.setRole(role);
        fees.setBranchCode(branchCode);
        StudentFees fees1 = feesRepository.save(fees);
        return mapToDTOFees(fees1);

    }

    @Override
    public StudentFeesDTO updateFees(Long id, StudentFees updatedFees,String role, String email)
    {
        checkPermission(role,email,"Put");
        StudentFees existing = feesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Standard Fees not found"));

        if (updatedFees.getStandardName() != null) existing.setStandardName(updatedFees.getStandardName());
        if (updatedFees.getMediumName() != null) existing.setMediumName(updatedFees.getMediumName());
        if (updatedFees.getFeesType() != null) existing.setFeesType(updatedFees.getFeesType());
        if (updatedFees.getApprovalDate() != null) existing.setApprovalDate(updatedFees.getApprovalDate());
        if (updatedFees.getFeesStatus() != null) existing.setFeesStatus(updatedFees.getFeesStatus());
        if (updatedFees.getFeesCollectionType() != null) existing.setFeesCollectionType(updatedFees.getFeesCollectionType());
        if (updatedFees.getTuitionFee() != 0) existing.setTuitionFee(updatedFees.getTuitionFee());
        if (updatedFees.getAdmissionFee() != 0) existing.setAdmissionFee(updatedFees.getAdmissionFee());
        if (updatedFees.getPracticalFee() != 0) existing.setPracticalFee(updatedFees.getPracticalFee());
        if (updatedFees.getComputerClassFee() != 0) existing.setComputerClassFee(updatedFees.getComputerClassFee());
        if (updatedFees.getExamFees() != 0) existing.setExamFees(updatedFees.getExamFees());
        if (updatedFees.getUniformFee() != 0) existing.setUniformFee(updatedFees.getUniformFee());
        if (updatedFees.getTransportBusFee() != 0) existing.setTransportBusFee(updatedFees.getTransportBusFee());
        if (updatedFees.getHostelFee() != 0) existing.setHostelFee(updatedFees.getHostelFee());
        if (updatedFees.getBuildingFundFee() != 0) existing.setBuildingFundFee(updatedFees.getBuildingFundFee());
        if (updatedFees.getLibraryFees() != 0) existing.setLibraryFees(updatedFees.getLibraryFees());
        if (updatedFees.getSportFees() != 0) existing.setSportFees(updatedFees.getSportFees());
        if (updatedFees.getFeesAmount() != null && updatedFees.getFeesAmount() != 0) existing.setFeesAmount(updatedFees.getFeesAmount());
        if (updatedFees.getDiscount() != 0) existing.setDiscount(updatedFees.getDiscount());
        if (updatedFees.getDiscountedAmount() != 0) existing.setDiscountedAmount(updatedFees.getDiscountedAmount());
        if (updatedFees.getTotalamount() != null && updatedFees.getTotalamount() != 0) existing.setTotalamount(updatedFees.getTotalamount());
        if (updatedFees.getLateFeeCharges() != 0) existing.setLateFeeCharges(updatedFees.getLateFeeCharges());
        if (updatedFees.getSfid() != null) existing.setSfid(updatedFees.getSfid());
        if (updatedFees.getCreatedByEmail() != null) existing.setCreatedByEmail(updatedFees.getCreatedByEmail());
        if (updatedFees.getRole() != null) existing.setRole(updatedFees.getRole());
        if (updatedFees.getBranchCode() != null) existing.setBranchCode(updatedFees.getBranchCode());

        StudentFees fees = feesRepository.save(existing);
        return mapToDTOFees(fees);

    }

    @Override
    public void deleteFees(Long id,String role, String email)
    {
        checkPermission(role,email,"Delete");
        feesRepository.deleteById(id);

    }

    @Override
    public StudentFeesDTO getFeesById(Long id,String role, String email)
    {
        checkPermission(role,email,"Get");
        StudentFees fees = feesRepository.findById(id).orElseThrow(() -> new RuntimeException("Fees Id Not Found"));
        return mapToDTOFees(fees);

    }

    @Override
    public List<StudentFeesDTO> getAllFees(String role, String email)
    {
        checkPermission(role,email,"Get");
        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        List<StudentFees> fees = feesRepository.getAllByBranchCode(branchCode);

        return fees.stream().map(this::mapToDTOFees)
            .collect(Collectors.toList());
    }

    public StudentFeesDTO mapToDTOFees(StudentFees fees) {
        StudentFeesDTO dto = new StudentFeesDTO();

        dto.setFid(fees.getFid());
        dto.setStudentName(fees.getStudentName());
        dto.setRollNo(fees.getRollNo());
        dto.setStandardName(fees.getStandardName());
        dto.setMediumName(fees.getMediumName());
        dto.setFeesType(fees.getFeesType());
        dto.setApprovalDate(fees.getApprovalDate());
        dto.setFeesStatus(fees.getFeesStatus());
        dto.setFeesCollectionType(fees.getFeesCollectionType());
        dto.setTuitionFee(fees.getTuitionFee());
        dto.setAdmissionFee(fees.getAdmissionFee());
        dto.setPracticalFee(fees.getPracticalFee());
        dto.setComputerClassFee(fees.getComputerClassFee());
        dto.setExamFees(fees.getExamFees());
        dto.setUniformFee(fees.getUniformFee());
        dto.setTransportBusFee(fees.getTransportBusFee());
        dto.setHostelFee(fees.getHostelFee());
        dto.setBuildingFundFee(fees.getBuildingFundFee());
        dto.setLibraryFees(fees.getLibraryFees());
        dto.setSportFees(fees.getSportFees());
        dto.setFeesAmount(fees.getFeesAmount());
        dto.setDiscount(fees.getDiscount());
        dto.setDiscountedAmount(fees.getDiscountedAmount());
        dto.setTotalamount(fees.getTotalamount());
        dto.setLateFeeCharges(fees.getLateFeeCharges());
        dto.setSfid(fees.getSfid());
        dto.setPaidAmount(fees.getPaidAmount());
        dto.setPendingAmount(fees.getPendingAmount());
        dto.setPaymentStatus(fees.getPaymentStatus());
        dto.setFeesPaymentType(fees.getFeesPaymentType());
        dto.setCreatedByEmail(fees.getCreatedByEmail());
        dto.setRole(fees.getRole());
        dto.setBranchCode(fees.getBranchCode());

        return dto;
    }



}
