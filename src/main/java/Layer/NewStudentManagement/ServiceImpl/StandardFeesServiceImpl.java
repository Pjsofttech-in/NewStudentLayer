package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.FeesFilterDTO;
import Layer.NewStudentManagement.DTO.StandardFeesRequestDTO;
import Layer.NewStudentManagement.Entity.*;
import Layer.NewStudentManagement.Repository.*;
import Layer.NewStudentManagement.Service.StandardFeesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

        // Fetch Medium
        StudentMedium medium = mediumRepository.findById(standardFees.getMedium().getMid())
                .orElseThrow(() -> new RuntimeException("Invalid medium ID"));
        standardFees.setMedium(medium);
        standardFees.setMediumName(medium.getMediumName());

        standardFees.setCreatedByEmail(email);
        standardFees.setRole(role);
        standardFees.setBranchCode(branchCode);

        String institutionType = standardFees.getInstitutionType();
        String academicYear=standardFees.getAcademicYear();

        if ("School".equalsIgnoreCase(institutionType)) {
            // School case
            StudentStandard standard = standardRepository.findById(standardFees.getStandard().getSid())
                    .orElseThrow(() -> new RuntimeException("Invalid standard ID"));

            boolean exists = standardFeesRepository.existsByStandardAndMediumAndBranchCodeAndAcademicYear(standard, medium, branchCode,academicYear);
            if (exists) {
                throw new RuntimeException("Fees already assigned for this standard and medium.");
            }

            standardFees.setStandard(standard);
            standardFees.setStandardName(standard.getStandardName());
        }

        else if ("College".equalsIgnoreCase(institutionType)) {
            // Graduation Type must be present
            if (standardFees.getGraduationType() == null || standardFees.getGraduationType().getId() == null) {
                throw new RuntimeException("Graduation type is required for College.");
            }

            StudentGraduationType graduationType = graduationTypeRepository.findById(standardFees.getGraduationType().getId())
                    .orElseThrow(() -> new RuntimeException("Invalid graduation type ID"));

            String graduationTypeName = graduationType.getGraduationType();
            standardFees.setGraduationType(graduationType);
            standardFees.setGraduationTypeName(graduationTypeName);

            // Jr. College logic
            if ("Jr.College".equalsIgnoreCase(graduationTypeName)) {

                StudentStandard standard = standardRepository.findById(standardFees.getStandard().getSid())
                        .orElseThrow(() -> new RuntimeException("Invalid standard ID"));

                StudentStream stream = streamRepository.findById(standardFees.getStream().getId())
                        .orElseThrow(() -> new RuntimeException("Invalid stream ID"));

                String groupName = standardFees.getGroupName();
                boolean exists = standardFeesRepository.existsByStandardAndStreamAndMediumAndBranchCodeAndAcademicYearAndGroupName(
                        standard, stream, medium, branchCode,academicYear,groupName);

                if (exists) {
                    throw new RuntimeException("Fees already assigned for this standard, stream, and medium.");
                }

                standardFees.setStandard(standard);
                standardFees.setStandardName(standard.getStandardName());
                standardFees.setStream(stream);
                standardFees.setStreamName(stream.getStream());
            }

            // UG/PG logic
            else {
                if (standardFees.getDegree() == null || standardFees.getDegree().getId() == null ||
                        standardFees.getDepartment() == null || standardFees.getDepartment().getId() == null) {
                    throw new RuntimeException("Degree and Department must be provided for UG/PG College students.");
                }
                StudentStream stream = streamRepository.findById(standardFees.getStream().getId())
                        .orElseThrow(() -> new RuntimeException("Invalid stream ID"));

                StudentDegreeName degree = degreeNameRepository.findById(standardFees.getDegree().getId())
                        .orElseThrow(() -> new RuntimeException("Invalid degree ID"));

                StudentDepartment department = departmentRepository.findById(standardFees.getDepartment().getId())
                        .orElseThrow(() -> new RuntimeException("Invalid department ID"));

                boolean exists = standardFeesRepository.existsByGraduationTypeAndDegreeAndDepartmentAndMediumAndBranchCodeAndAcademicYear(
                        graduationType, degree, department, medium, branchCode,academicYear);

                if (exists) {
                    throw new RuntimeException("Fees already assigned for this graduation type, degree, department, and medium.");
                }

                standardFees.setStream(stream);
                standardFees.setStreamName(stream.getStream());
                standardFees.setDegree(degree);
                standardFees.setDegreeName(degree.getDegreeName());
                standardFees.setDepartment(department);
                standardFees.setDepartmentName(department.getDepartmentName());
            }
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
    public List<StandardFeesRequestDTO> filterFees(String role, String email, FeesFilterDTO filterDTO) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to filter fees.");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        String institutionType = Optional.ofNullable(filterDTO.getInstitutionType()).orElse("").trim();

        // Medium
        String mediumName = Optional.ofNullable(filterDTO.getMediumName()).orElseThrow(() ->
                new RuntimeException("Medium is required")).trim();
        List<Long> mediumIds = mediumRepository.findIdsByName(mediumName, branchCode);
        if (mediumIds.size() != 1) throw new RuntimeException("Invalid or duplicate medium");
        Long mediumId = mediumIds.get(0);

        List<StudentStandardFees> feesList;

        // SCHOOL
        if ("school".equalsIgnoreCase(institutionType)) {
            String standardName = Optional.ofNullable(filterDTO.getStandardName()).orElseThrow(() ->
                    new RuntimeException("Standard is required for school")).trim();
            List<Long> standardIds = standardRepository.findIdsByName(standardName, branchCode);
            if (standardIds.size() != 1) throw new RuntimeException("Invalid or duplicate standard");
            Long standardId = standardIds.get(0);

            feesList = standardFeesRepository.findForSchool(standardId, mediumId, branchCode);
        }

        // COLLEGE
        else if ("college".equalsIgnoreCase(institutionType)) {
            String streamName = Optional.ofNullable(filterDTO.getStreamName()).orElseThrow(() ->
                    new RuntimeException("Stream name is required")).trim();
            List<Long> streamIds = streamRepository.findIdsByNameAndBranchCode(streamName, branchCode);
            if (streamIds.size() != 1) throw new RuntimeException("Invalid or duplicate stream");
            Long streamId = streamIds.get(0);

            String graduationType = Optional.ofNullable(filterDTO.getGraduationTypeName()).orElseThrow(() ->
                    new RuntimeException("Graduation type is required")).trim();
            List<Long> graduationTypeIds = graduationTypeRepository.findIdsByNameAndStreamAndBranchCode(graduationType, streamId, branchCode);
            if (graduationTypeIds.size() != 1) throw new RuntimeException("Invalid or duplicate graduation type");
            Long graduationTypeId = graduationTypeIds.get(0);

            // UG/PG
            if (filterDTO.getDegreeName() != null && filterDTO.getDepartmentName() != null) {
                String degreeName = filterDTO.getDegreeName().trim();
                List<Long> degreeIds = degreeNameRepository.findIdsByNameAndGraduationTypeAndBranchCode(degreeName, graduationTypeId, branchCode);
                if (degreeIds.size() != 1) throw new RuntimeException("Invalid or duplicate degree name");
                Long degreeId = degreeIds.get(0);

                String departmentName = filterDTO.getDepartmentName().trim();
                List<Long> departmentIds = departmentRepository.findIdsByNameAndDegreeAndBranchCode(departmentName, degreeId, branchCode);
                if (departmentIds.size() != 1) throw new RuntimeException("Invalid or duplicate department");
                Long departmentId = departmentIds.get(0);

                feesList = standardFeesRepository.findForUGPG(mediumId, streamId, degreeId, departmentId, branchCode);
            }
            // JR.COLLEGE
            else {
                String standardName = Optional.ofNullable(filterDTO.getStandardName()).orElseThrow(() ->
                        new RuntimeException("Standard is required for Jr. College")).trim();
                List<Long> standardIds = standardRepository.findIdsByName(standardName, branchCode);
                if (standardIds.size() != 1) throw new RuntimeException("Invalid or duplicate standard");
                Long standardId = standardIds.get(0);

                String groupName = Optional.ofNullable(filterDTO.getGroupName()).orElseThrow(() ->
                        new RuntimeException("Group name is required for Jr. College")).trim();

                feesList = standardFeesRepository.findForJrCollege(
                        standardId, mediumId, streamId, graduationTypeId, groupName, branchCode
                );
            }

        } else {
            throw new RuntimeException("Invalid institution type: " + institutionType);
        }

        return feesList.stream().map(this::mapToDto).collect(Collectors.toList());
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
        dto.setInstitutionType(entity.getInstitutionType());
        dto.setStreamName(entity.getStreamName());
        dto.setGraduationTypeName(entity.getGraduationTypeName());
        dto.setDegreeName(entity.getDegreeName());
        dto.setDepartmentName(entity.getDepartmentName());
        dto.setGroupName(entity.getGroupName());
        dto.setAcademicYear(entity.getAcademicYear());
        dto.setBranchCode(entity.getBranchCode());
        dto.setCreatedByEmail(entity.getCreatedByEmail());
        dto.setRole(entity.getRole());

        return dto;
    }


}

