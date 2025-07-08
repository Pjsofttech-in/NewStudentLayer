package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StandardFeesRequestDTO;
import Layer.NewStudentManagement.Entity.*;
import Layer.NewStudentManagement.Repository.*;
import Layer.NewStudentManagement.Service.StandardFeesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StandardFeesServiceImpl implements StandardFeesService
{

    @Autowired
    StaffService staffService;

    @Autowired
    StandardFeesRepository standardFeesRepository;

    @Autowired
    StandardRepository standardRepository;

    @Autowired
    MediumRepository mediumRepository;

    @Autowired
    GraduationTypeRepository graduationTypeRepository;

    @Autowired
    StreamRepository streamRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    DegreeNameRepository degreeNameRepository;


    private void checkPermission(String role, String email, String action) {
        if (!staffService.hasPermission(role, email, action)) {
            throw new RuntimeException("You don't have permission to " + action.toLowerCase() + " student");
        }
    }

    @Override
    public StandardFeesRequestDTO createStandardFees(String role, String email, StudentStandardFees standardFees) {
        checkPermission(role, email, "Post");
        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        // Fetch Medium by ID
        StudentMedium medium = mediumRepository.findById(standardFees.getMedium().getMid())
                .orElseThrow(() -> new RuntimeException("Invalid medium ID"));
        standardFees.setMedium(medium);
        standardFees.setMediumName(medium.getMediumName());

        standardFees.setCreatedByEmail(email);
        standardFees.setRole(role);
        standardFees.setBranchCode(branchCode);

        String institutionType = standardFees.getInstitutionType();

        // School: standard + medium
        if ("School".equalsIgnoreCase(institutionType)) {
            StudentStandard standard = standardRepository.findById(standardFees.getStandard().getSid())
                    .orElseThrow(() -> new RuntimeException("Invalid standard ID"));

            boolean exists = standardFeesRepository.existsByStandardAndMediumAndBranchCode(standard, medium, branchCode);
            if (exists) {
                throw new RuntimeException("Fees already assigned for this standard and medium.");
            }

            standardFees.setStandard(standard);
            standardFees.setStandardName(standard.getStandardName());
        }

        // Jr. College: standard + stream + medium + graduationType (Jr.College)
        else if ("College".equalsIgnoreCase(institutionType) &&
                "Jr.College".equalsIgnoreCase(standardFees.getGraduationTypeName())) {

            StudentStandard standard = standardRepository.findById(standardFees.getStandard().getSid())
                    .orElseThrow(() -> new RuntimeException("Invalid standard ID"));

            StudentStream stream = streamRepository.findById(standardFees.getStream().getId())
                    .orElseThrow(() -> new RuntimeException("Invalid stream ID"));

            StudentGraduationType graduationType = graduationTypeRepository.findById(standardFees.getGraduationType().getId())
                    .orElseThrow(() -> new RuntimeException("Invalid graduation type ID"));

            boolean exists = standardFeesRepository.existsByStandardAndStreamAndMediumAndBranchCode(
                    standard, stream, medium, branchCode);

            if (exists) {
                throw new RuntimeException("Fees already assigned for this standard, stream, and medium.");
            }

            standardFees.setStandard(standard);
            standardFees.setStandardName(standard.getStandardName());
            standardFees.setStream(stream);
            standardFees.setStreamName(stream.getStream());
            standardFees.setGraduationType(graduationType);
            standardFees.setGraduationTypeName(graduationType.getGraduationType());
        }

        //  UG/PG: graduationType + degree + department + medium
        else if ("College".equalsIgnoreCase(institutionType)) {

            StudentGraduationType graduationType = graduationTypeRepository.findById(standardFees.getGraduationType().getId())
                    .orElseThrow(() -> new RuntimeException("Invalid graduation type ID"));

            StudentDegreeName degree = degreeNameRepository.findById(standardFees.getDegree().getId())
                    .orElseThrow(() -> new RuntimeException("Invalid degree ID"));

            StudentDepartment department = departmentRepository.findById(standardFees.getDepartment().getId())
                    .orElseThrow(() -> new RuntimeException("Invalid department ID"));

            boolean exists = standardFeesRepository.existsByGraduationTypeAndDegreeAndDepartmentAndMediumAndBranchCode(
                    graduationType, degree, department, medium, branchCode);

            if (exists) {
                throw new RuntimeException("Fees already assigned for this graduation type, degree, department, and medium.");
            }

            standardFees.setGraduationType(graduationType);
            standardFees.setGraduationTypeName(graduationType.getGraduationType());
            standardFees.setDegree(degree);
            standardFees.setDegreeName(degree.getDegreeName());
            standardFees.setDepartment(department);
            standardFees.setDepartmentName(department.getDepartmentName());
        }

        else {
            throw new RuntimeException("Invalid institution type or missing data.");
        }
        standardFeesRepository.save(standardFees);
        return mapToDto(standardFees);
    }

    @Override
    public StandardFeesRequestDTO updateStandardFees(String role, String email, Long sfid, StudentStandardFees updatedFees)
    {
        checkPermission(role,email,"Put");

        StudentStandardFees existing = standardFeesRepository.findById(sfid)
                .orElseThrow(() -> new RuntimeException("Standard Fees not found"));

        if (updatedFees.getStandardName() != null) existing.setStandardName(updatedFees.getStandardName());
        if (updatedFees.getMediumName() != null) existing.setMediumName(updatedFees.getMediumName());
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
        if (updatedFees.getGST() != 0) existing.setGST(updatedFees.getGST());
        if (updatedFees.getFeesAmount() != null && updatedFees.getFeesAmount() != 0) existing.setFeesAmount(updatedFees.getFeesAmount());
        if (updatedFees.getBranchCode() != null) existing.setBranchCode(updatedFees.getBranchCode());

        StudentStandardFees standardFees = standardFeesRepository.save(existing);
        return mapToDto(standardFees);

    }

    @Override
    public StandardFeesRequestDTO getStandardFeesById(String role, String email, Long sfid)
    {
        checkPermission(role,email,"Get");

        StudentStandardFees standardFees = standardFeesRepository.findById(sfid)
                .orElseThrow(()->new RuntimeException("standardFees not found"));
        return mapToDto(standardFees);

    }

    @Override
    public List<StandardFeesRequestDTO> getAllStandardFees(String role, String email)
    {
        checkPermission(role,email,"Get");
        String branchCode = staffService.fetchBranchCodeByRole(role,email);

        List<StudentStandardFees> standardFees = standardFeesRepository.findAllByBranchCode(branchCode);
        return standardFees.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

    }

    @Override
    public void deleteStandardFee(String role, String email, Long sfid)
    {
        checkPermission(role,email,"Delete");

        standardFeesRepository.deleteById(sfid);

    }

    @Override
    public List<StandardFeesRequestDTO> getStandardFeesByStandard(String role, String email, String standardName, String mediumName) {
        checkPermission(role, email, "Get");

        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        List<StudentStandardFees> standardFees = standardFeesRepository
                .findByStandardAndMediumAndBranch(standardName, mediumName, branchCode);

        return standardFees.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    public StandardFeesRequestDTO mapToDto(StudentStandardFees entity) {
        StandardFeesRequestDTO dto = new StandardFeesRequestDTO();
        dto.setSfid(entity.getSfid());
        dto.setStandardId(entity.getStandard() != null ? entity.getStandard().getSid() : null);
        dto.setMediumId(entity.getMedium() != null ? entity.getMedium().getMid() : null);

        dto.setTuitionFee(entity.getTuitionFee());
        dto.setAdmissionFee(entity.getAdmissionFee());
        dto.setPracticalFee(entity.getPracticalFee());
        dto.setComputerClassFee(entity.getComputerClassFee());
        dto.setExamFees(entity.getExamFees());
        dto.setUniformFee(entity.getUniformFee());
        dto.setTransportBusFee(entity.getTransportBusFee());
        dto.setHostelFee(entity.getHostelFee());
        dto.setBuildingFundFee(entity.getBuildingFundFee());
        dto.setLibraryFees(entity.getLibraryFees());
        dto.setSportFees(entity.getSportFees());
        dto.setGST(entity.getGST());
        dto.setFeesAmount(entity.getFeesAmount());
        dto.setStandardName(entity.getStandardName());
        dto.setMediumName(entity.getMediumName());

        dto.setBranchCode(entity.getBranchCode());
        dto.setCreatedByEmail(entity.getCreatedByEmail());
        dto.setRole(entity.getRole());

        return dto;
    }


}

