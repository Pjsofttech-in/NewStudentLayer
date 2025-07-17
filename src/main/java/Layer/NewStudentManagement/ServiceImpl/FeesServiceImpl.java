package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.FeeScheduleDTO;
import Layer.NewStudentManagement.DTO.StudentFeesDTO;
import Layer.NewStudentManagement.DTO.StudentFeesFilterRequest;
import Layer.NewStudentManagement.Entity.*;
import Layer.NewStudentManagement.Pagination.StudentFeesSpecification;
import Layer.NewStudentManagement.Repository.*;
import Layer.NewStudentManagement.Service.FeesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    private MediumRepository mediumRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DegreeNameRepository degreeNameRepository;

    @Autowired
    private StreamRepository streamRepository;


    private void checkPermission(String role, String email, String action) {
        if (!staffService.hasPermission(role, email, action)) {
            throw new RuntimeException("You don't have permission to " + action.toLowerCase() + " Fees");
        }
    }

    @Override
    public StudentFeesDTO assignFeesToStudent(String role, String email, StudentFees fees) {
        checkPermission(role, email, "Post");

        // Fetch student
        StudentEntity student = studentRepository.findById(fees.getStudent().getId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        // Determine type
        boolean isUGPG = student.getDegreeName() != null && student.getDepartment() != null;
        boolean isJrCollege = student.getStream() != null && !isUGPG;

        // === UG/PG ===
        if (isUGPG) {
            if (fees.getDegree() == null || fees.getDegree().getId() == null ||
                    fees.getDepartment() == null || fees.getDepartment().getId() == null) {
                throw new RuntimeException("Degree and Department must be provided for UG/PG student.");
            }

            // Check if fees already assigned
            if (feesRepository.existsUGPGFees(student, fees.getDegree().getId(), fees.getDepartment().getId())) {
                throw new RuntimeException("Fees already assigned for this student, degree, and department.");
            }

            // Fetch and set degree and department
            StudentDegreeName degree = degreeNameRepository.findById(fees.getDegree().getId())
                    .orElseThrow(() -> new RuntimeException("Invalid degree ID"));
            StudentDepartment department = departmentRepository.findById(fees.getDepartment().getId())
                    .orElseThrow(() -> new RuntimeException("Invalid department ID"));

            fees.setDegree(degree);
            fees.setDegreeName(degree.getDegreeName());
            fees.setDepartment(department);
            fees.setDepartmentName(department.getDepartmentName());
        }

        // === Jr. College ===
        else if (isJrCollege) {
            if (fees.getStream() == null || fees.getStream().getId() == null) {
                throw new RuntimeException("Stream must be provided for Jr. College student.");
            }

            if (feesRepository.existsJrCollegeFees(student, fees.getStream().getStream())) {
                throw new RuntimeException("Fees already assigned for this student and stream.");
            }

            StudentStream stream = streamRepository.findById(fees.getStream().getId())
                    .orElseThrow(() -> new RuntimeException("Invalid stream ID"));
            fees.setStream(stream);
            fees.setStreamName(stream.getStream());
        }

        else {
            throw new RuntimeException("Student must be either Jr. College or UG/PG to assign fees.");
        }

        // === Optional: Standard (in case you also support standard-based logic later) ===
        if (fees.getStandard() != null && fees.getStandard().getSid() != null) {
            StudentStandard standard = standardRepository.findById(fees.getStandard().getSid())
                    .orElseThrow(() -> new RuntimeException("Standard not found"));
            fees.setStandard(standard);
            fees.setStandardName(standard.getStandardName());
        }

        // === Optional: Medium ===
        if (fees.getMedium() != null && fees.getMedium().getMid() != null) {
            StudentMedium medium = mediumRepository.findById(fees.getMedium().getMid())
                    .orElseThrow(() -> new RuntimeException("Medium not found"));
            fees.setMedium(medium);
            fees.setMediumName(medium.getMediumName());
        }

        // === Set basic details ===
        fees.setStudent(student);
        fees.setStudentName(student.getFullName());
        fees.setRollNo(student.getRollNo());
        fees.setApprovalDate(fees.getApprovalDate()); // From frontend
        fees.setDiscount(fees.getDiscount());// From frontend
        fees.setFeesStatus(fees.getFeesStatus());
        fees.setCreatedByEmail(email);
        fees.setRole(role);
        fees.setBranchCode(branchCode);

        // === Frontend must send all fee values (backend doesn't calculate) ===
        fees.setPendingAmount(fees.getTotalamount()); // initially all is pending

        List<StudentFeeSchedule> scheduleList = new ArrayList<>();
        if (fees.getScheduleList() != null && !fees.getScheduleList().isEmpty()) {
            for (StudentFeeSchedule item : fees.getScheduleList()) {
                StudentFeeSchedule schedule = new StudentFeeSchedule();
                schedule.setFeesType(item.getFeesType());
                schedule.setMonth(item.getMonth());
                schedule.setCollectAmount(item.getCollectAmount());
                schedule.setPaid(false);
                schedule.setStudentFees(fees);
                scheduleList.add(schedule);
            }
        }
        fees.setScheduleList(scheduleList);
        StudentFees savedFees = feesRepository.save(fees);
        return mapToDTOFees(savedFees);
    }

    @Override
    public StudentFeesDTO updateFees(Long id, StudentFees updatedFees,String role, String email)
    {
        checkPermission(role,email,"Put");
        StudentFees existing = feesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Standard Fees not found"));

        if (updatedFees.getStandardName() != null) existing.setStandardName(updatedFees.getStandardName());
        if (updatedFees.getMediumName() != null) existing.setMediumName(updatedFees.getMediumName());
//        if (updatedFees.getFeesType() != null) existing.setFeesType(updatedFees.getFeesType());
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
        if (updatedFees.getTotalamount() != null && updatedFees.getTotalamount() != 0) existing.setTotalamount(updatedFees.getTotalamount());
//        if (updatedFees.getLateFeeCharges() != 0) existing.setLateFeeCharges(updatedFees.getLateFeeCharges());
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


//    @Override
//    public List<StudentFeesDTO> getAllFees(String role, String email)
//    {
//        checkPermission(role,email,"Get");
//        String branchCode = staffService.fetchBranchCodeByRole(role, email);
//
//        List<StudentFees> fees = feesRepository.getAllByBranchCode(branchCode);
//
//        return fees.stream().map(this::mapToDTOFees)
//            .collect(Collectors.toList());
//    }

    @Override
    public List<StudentFeesDTO> getAllFeesForStudent(Long studentId,String role, String email)
    {
        checkPermission(role,email,"Get");
        List<StudentFees> feesList = feesRepository.findFeesByStudentId(studentId);
        return feesList.stream().map(this::mapToDTOFees).toList();
    }


    @Override
    public Page<StudentFeesDTO> filterStudentFees(StudentFeesFilterRequest request, String role, String email, int page, int size) {
        checkPermission(role, email, "Get");

        Specification<StudentFees> spec = Specification
                .where(StudentFeesSpecification.hasStudentName(request.getStudentName()))
                .and(StudentFeesSpecification.hasFeesStatus(request.getFeesStatus()));

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<StudentFees> studentFeesPage = feesRepository.findAll(spec, pageRequest);

        return studentFeesPage.map(this::mapToDTOFees);
    }


    public StudentFeesDTO mapToDTOFees(StudentFees fees) {
        StudentFeesDTO dto = new StudentFeesDTO();

        dto.setFid(fees.getFid());
        dto.setStudentName(fees.getStudentName());
        dto.setRollNo(fees.getRollNo());
        dto.setStandardName(fees.getStandardName());
        dto.setMediumName(fees.getMediumName());
        dto.setStreamName(fees.getStreamName());
        dto.setDegreeName(fees.getDegreeName());
        dto.setDepartmentName(fees.getDepartmentName());
//        dto.setFeesType(fees.getFeesType());
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
        dto.setGST(fees.getGST());
        dto.setTotalamount(fees.getTotalamount());
//        dto.setLateFeeCharges(fees.getLateFeeCharges());
        dto.setSfid(fees.getSfid());
        dto.setPaidAmount(fees.getPaidAmount());
        dto.setPendingAmount(fees.getPendingAmount());
//        dto.setFeesPaymentType(fees.getFeesPaymentType());
        dto.setCreatedByEmail(fees.getCreatedByEmail());
        dto.setRole(fees.getRole());
        dto.setBranchCode(fees.getBranchCode());

        if (fees.getScheduleList() != null && !fees.getScheduleList().isEmpty()) {
            List<FeeScheduleDTO> scheduleList = fees.getScheduleList().stream()
                    .map(s -> {
                        FeeScheduleDTO sdto = new FeeScheduleDTO();
                        sdto.setFeesType(s.getFeesType());
                        sdto.setMonth(s.getMonth());
                        sdto.setPaid(s.isPaid());
                        sdto.setCollectAmount(s.getCollectAmount());
                        return sdto;
                    }).collect(Collectors.toList());

            dto.setScheduleList(scheduleList);
        }

        return dto;
    }


}
