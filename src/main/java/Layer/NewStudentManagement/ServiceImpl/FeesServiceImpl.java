package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.*;
import Layer.NewStudentManagement.Entity.*;
import Layer.NewStudentManagement.Pagination.StudentFeesSpecification;
import Layer.NewStudentManagement.Repository.*;
import Layer.NewStudentManagement.Service.FeesService;
import Layer.NewStudentManagement.Util.HelperUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FeesServiceImpl implements FeesService {
    @Autowired
    private FeesRepository feesRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private StaffService staffService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StandardRepository standardRepository;

    @Autowired
    private MediumRepository mediumRepository;


    @Autowired
    private DegreeNameRepository degreeNameRepository;

    @Autowired
    private StreamRepository streamRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private FeesScheduleRepository feesScheduleRepository;

    @Autowired
    private FeesCollectRepository feesCollectRepository;


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

        fees.setInstitutionType(fees.getInstitutionType());
        fees.setRollNo(student.getRollNo());
        boolean isUGPG = student.getDegreeName() != null && student.getStream() != null;
        boolean isJrCollege = student.getStream() != null && student.getGroupName() != null && !isUGPG;

        // === Fetch and set Degree & Department if UG/PG ===
        if (isUGPG) {
            if (fees.getDegree() == null || fees.getDegree().getId() == null ||
                    fees.getStream() == null || fees.getStream().getId() == null) {
                throw new RuntimeException("Stream, Degree and Department  must be provided for UG/PG student.");
            }

            StudentStream stream = streamRepository.findById(fees.getStream().getId())
                    .orElseThrow(() -> new RuntimeException("Invalid stream ID"));
            StudentDegreeName degree = degreeNameRepository.findById(fees.getDegree().getId())
                    .orElseThrow(() -> new RuntimeException("Invalid degree ID"));

            fees.setStream(stream);
            fees.setStreamName(stream.getStream());
            fees.setDegree(degree);
            fees.setDegreeName(degree.getDegreeName());

        }

        // === Fetch and set Stream if Jr. College ===
        if (isJrCollege) {
            if (fees.getStream() == null || fees.getStream().getId() == null) {
                throw new RuntimeException("Stream must be provided for Jr. College student.");
            }

            StudentStream stream = streamRepository.findById(fees.getStream().getId())
                    .orElseThrow(() -> new RuntimeException("Invalid stream ID"));
            fees.setStream(stream);
            fees.setStreamName(stream.getStream());
        }

        // === Optional: Standard ===
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

        // === Optional: Group ===
        if (fees.getGroup() != null && fees.getGroup().getId() != null) {
            StudentGroup group = groupRepository.findById(fees.getGroup().getId())
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            fees.setGroup(group);
        }

        // === Existence Checks AFTER setting entities ===

        // UGPG check
        if (isUGPG && feesRepository.existsUGPGFees(student, fees.getDegree().getId())) {
            throw new RuntimeException("Fees already assigned for this student, degree, and department.");
        }

        // Jr College check
        if (isJrCollege && feesRepository.existsJrCollegeFees(student, fees.getStandard().getStandardName(), fees.getStream().getStream())) {
            throw new RuntimeException("Fees already assigned for this student and stream.");
        }

        // Standard + Medium check
        if (fees.getStandard() != null && fees.getMedium() != null &&
                fees.getStream() == null && fees.getDegree() == null) {
            if (feesRepository.existsByStandardAndMedium(student,
                    fees.getStandard().getSid(), fees.getMedium().getMid())) {
                throw new RuntimeException("Fees already assigned for this student with same Standard and Medium.");
            }
        }

        if (fees.getStandard() != null && fees.getMedium() != null &&
                fees.getStream() != null && fees.getGroup() != null) {
            if (feesRepository.existsFees(student,
                    fees.getStandard().getSid(),
                    fees.getMedium().getMid(),
                    fees.getStream().getId(),
                    fees.getGroup().getId())) {
                throw new RuntimeException("Fees already assigned for this student with same Standard, Medium, Stream, and Group.");
            }
        }

        // Medium + Stream + Degree + Department check
        if (fees.getMedium() != null && fees.getStream() != null &&
                fees.getDegree() != null) {
            if (feesRepository.existsByMediumStreamDegreeDepartment(student,
                    fees.getMedium().getMid(),
                    fees.getStream().getId(),
                    fees.getDegree().getId())) {
                throw new RuntimeException("Fees already assigned for this student with same Medium, Stream, Degree, and Department.");
            }
        }

        // === Set base details ===
        fees.setStudent(student);
        fees.setStudentName(student.getFullName());
        fees.setRollNo(student.getRollNo());
        fees.setApprovalDate(fees.getApprovalDate());
        fees.setDiscount(fees.getDiscount());
        fees.setFeesStatus(fees.getFeesStatus());
        fees.setCreatedByEmail(email);
        fees.setRole(role);
        fees.setBranchCode(branchCode);
        fees.setPendingAmount(fees.getTotalamount());

        // === Schedule mapping ===
        List<StudentFeeSchedule> scheduleList = new ArrayList<>();
        if (fees.getScheduleList() != null && !fees.getScheduleList().isEmpty()) {
            for (StudentFeeSchedule item : fees.getScheduleList()) {
                StudentFeeSchedule schedule = new StudentFeeSchedule();
                schedule.setFeesType(item.getFeesType());
                schedule.setMonth(item.getMonth());
                schedule.setCollectAmount(item.getCollectAmount());
                schedule.setPaid(false);
                schedule.setDueDate(item.getDueDate());
                schedule.setStudentFees(fees);
                scheduleList.add(schedule);
            }
        }
        fees.setScheduleList(scheduleList);

        StudentFees savedFees = feesRepository.save(fees);
        return mapToDTOFees(savedFees);
    }


    @Override
    public StudentFeesDTO updateFees(Long id, StudentFees updatedFees, String role, String email) {
        checkPermission(role, email, "Put");
        StudentFees existing = feesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Standard Fees not found"));

        if (updatedFees.getStandardName() != null) existing.setStandardName(updatedFees.getStandardName());
        if (updatedFees.getMediumName() != null) existing.setMediumName(updatedFees.getMediumName());
//        if (updatedFees.getFeesType() != null) existing.setFeesType(updatedFees.getFeesType());
        if (updatedFees.getApprovalDate() != null) existing.setApprovalDate(updatedFees.getApprovalDate());
        if (updatedFees.getFeesStatus() != null) existing.setFeesStatus(updatedFees.getFeesStatus());
        if (updatedFees.getFeesCollectionType() != null)
            existing.setFeesCollectionType(updatedFees.getFeesCollectionType());
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
        if (updatedFees.getFeesAmount() != null && updatedFees.getFeesAmount() != 0)
            existing.setFeesAmount(updatedFees.getFeesAmount());
        if (updatedFees.getTotalamount() != null && updatedFees.getTotalamount() != 0)
            existing.setTotalamount(updatedFees.getTotalamount());
//        if (updatedFees.getLateFeeCharges() != 0) existing.setLateFeeCharges(updatedFees.getLateFeeCharges());
        if (updatedFees.getSfid() != null) existing.setSfid(updatedFees.getSfid());
        if (updatedFees.getCreatedByEmail() != null) existing.setCreatedByEmail(updatedFees.getCreatedByEmail());
        if (updatedFees.getRole() != null) {
            existing.setRole(updatedFees.getRole());
        }
        if (updatedFees.getBranchCode() != null) existing.setBranchCode(updatedFees.getBranchCode());

        StudentFees fees = feesRepository.save(existing);
        return mapToDTOFees(fees);

    }

    @Override
    public void deleteFees(Long id, String role, String email) {
        checkPermission(role, email, "Delete");
        feesRepository.deleteById(id);

    }

    @Override
    public StudentFeesDTO getFeesById(Long id, String role, String email) {
        checkPermission(role, email, "Get");
        StudentFees fees = feesRepository.findById(id).orElseThrow(() -> new RuntimeException("Fees Id Not Found"));
        return mapToDTOFees(fees);

    }

    @Override
    public List<StudentFeesDTO> getAllFeesForStudent(Long studentId, String role, String email) {
        checkPermission(role, email, "Get");
        List<StudentFees> feesList = feesRepository.findFeesByStudentId(studentId);
        return feesList.stream().map(this::mapToDTOFees).toList();
    }


    @Override
    public Page<StudentFeesDTO> getAllFeesWithFilter(FeesFilterDTO filterDTO, String branchCode, int page, int size, String sort) {
        String[] arr = sort.split(",");
        Sort.Direction dir = "DESC".equalsIgnoreCase(arr[1]) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, arr[0]));

        if (StringUtils.isNotBlank(filterDTO.getCreatedByName())) {
            CreatedByResponseDTO response = staffService.getCreatorByName(filterDTO.getCreatedByName()).block();
            String creatorEmail = Objects.isNull(response) ? "null" : response.getName();
            filterDTO.setCreatedByEmail(creatorEmail);
        }
        Page<StudentFees> fees = feesRepository.findAll(
                StudentFeesSpecification.filterByDTOAndBranchCode(filterDTO, branchCode),
                pageable
        );
        Page<StudentFeesDTO> dtoPage = fees.map(this::mapToDTOFees);
        return dtoPage;
    }


    @Override
    public FeesRevenueProjection getFeesRevenueByBranch(String role, String email, String timeFrame, LocalDate startDate, LocalDate endDate, FeesRevenueFilterDTO filters) {

        if ("SUPERADMIN".equalsIgnoreCase(role)) {

            checkPermission(role, email, "GET");

            if (filters.getBranchCode() != null && !filters.getBranchCode().isBlank()) {
                return calculateRevenue(filters.getBranchCode(), timeFrame, startDate, endDate, filters);
            }

            List<String> allBranchCodes = staffService.getBranchCodesByInstituteEmail(email);

            Double totalFees = 0.0;
            Double totalPaid = 0.0;
            Double totalPending = 0.0;

            for (String branch : allBranchCodes) {
                FeesRevenueProjection rp = calculateRevenue(branch, timeFrame, startDate, endDate, filters);

                totalFees += rp.getTotalFees() != null ? rp.getTotalFees() : 0.0;
                totalPaid += rp.getTotalPaid() != null ? rp.getTotalPaid() : 0.0;
                totalPending += rp.getTotalPending() != null ? rp.getTotalPending() : 0.0;
            }

            double finalFees = totalFees;
            double finalPaid = totalPaid;
            double finalPending = totalPending;

            return new FeesRevenueProjection() {
                @Override
                public Double getTotalFees() {
                    return finalFees;
                }

                @Override
                public Double getTotalPaid() {
                    return finalPaid;
                }

                @Override
                public Double getTotalPending() {
                    return finalPending;
                }
            };
        }

        checkPermission(role, email, "Get");

        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        return calculateRevenue(branchCode, timeFrame, startDate, endDate, filters);
    }

    private FeesRevenueProjection calculateRevenue(String branchCode, String timeFrame, LocalDate startDate, LocalDate endDate, FeesRevenueFilterDTO filters) {

        LocalDate calculatedStartDate = null;
        LocalDate calculatedEndDate = LocalDate.now();

        if (timeFrame != null && !timeFrame.isBlank()) {
            if ("custom".equalsIgnoreCase(timeFrame) && startDate != null && endDate != null) {
                calculatedStartDate = startDate;
                calculatedEndDate = endDate;
            } else {
                switch (timeFrame.toLowerCase()) {
                    case "today" -> calculatedStartDate = calculatedEndDate;
                    case "7days" -> calculatedStartDate = calculatedEndDate.minusDays(6);
                    case "30days" -> calculatedStartDate = calculatedEndDate.minusDays(29);
                    case "365days" -> calculatedStartDate = calculatedEndDate.minusDays(364);
                    case "all" -> {
                        calculatedStartDate = null;
                        calculatedEndDate = null;
                    }
                    default -> {
                        calculatedStartDate = null;
                        calculatedEndDate = null;
                    }
                }
            }
        } else {
            calculatedStartDate = startDate;
            calculatedEndDate = endDate;
        }

        filters.setBranchCode(branchCode);

        Specification<StudentFees> spec =
                StudentFeesSpecification.withFilters(branchCode, calculatedStartDate, calculatedEndDate, filters);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<StudentFees> root = cq.from(StudentFees.class);

        Predicate predicate = spec.toPredicate(root, cq, cb);
        cq.where(predicate);

        cq.multiselect(
                cb.sum(root.get("totalamount")).alias("totalFees"),
                cb.sum(root.get("paidAmount")).alias("totalPaid"),
                cb.sum(root.get("pendingAmount")).alias("totalPending")
        );

        Tuple result = entityManager.createQuery(cq).getSingleResult();

        return new FeesRevenueProjection() {
            @Override
            public Double getTotalFees() {
                Double v = result.get("totalFees", Double.class);
                return v != null ? v : 0.0;
            }

            @Override
            public Double getTotalPaid() {
                Double v = result.get("totalPaid", Double.class);
                return v != null ? v : 0.0;
            }

            @Override
            public Double getTotalPending() {
                Double v = result.get("totalPending", Double.class);
                return v != null ? v : 0.0;
            }
        };
    }


    @Override
    public FeesRevenueProjection getFeesRevenueByStudentId(String role, String email, Long studentId) {
        checkPermission(role, email, "Get");
        return feesRepository.getFeesRevenueByStudentId(studentId);
    }

    public Map<String, List<FeesScheduleChartDTO>> getMonthlyReport(String role, String email, int academicYear, String branchCodeFilter) {
        checkPermission(role, email, "Get");

        Map<String, List<FeesScheduleChartDTO>> map = new HashMap<>();

        if ("SUPERADMIN".equalsIgnoreCase(role)) {

            List<String> branchCodes = staffService.getBranchCodesByInstituteEmail(email);

            if (branchCodes == null || branchCodes.isEmpty()) {
                throw new RuntimeException("No branch codes found for this Superadmin");
            }

            if (branchCodeFilter != null && !branchCodeFilter.isEmpty()) {
                if (!branchCodes.contains(branchCodeFilter)) {
                    throw new RuntimeException("Invalid branchCode for this Superadmin");
                }

                List<FeesScheduleChartDTO> listOfFeesScheduleByMonth = getListOfFeesScheduleByMonth(academicYear, branchCodeFilter);
                map.put(branchCodeFilter, listOfFeesScheduleByMonth);
            }

            for (String brCode : branchCodes) {
                List<FeesScheduleChartDTO> listOfFeesScheduleByMonth = getListOfFeesScheduleByMonth(academicYear, branchCodeFilter);
                map.put(brCode, listOfFeesScheduleByMonth);
            }
            return map;
        }

        String brCode = staffService.fetchBranchCodeByRole(role, email);
        List<FeesScheduleChartDTO> listOfFeesScheduleByMonth = getListOfFeesScheduleByMonth(academicYear, brCode);
        map.put(brCode, listOfFeesScheduleByMonth);
        return map;
    }

    private List<FeesScheduleChartDTO> getListOfFeesScheduleByMonth(int academicYear, String branchCode) {
        List<FeesScheduleChartProjection> paidList = feesScheduleRepository.findByAcademicYearAndBranchCode(academicYear, 1, branchCode);
        List<FeesScheduleChartProjection> unpaidList = feesScheduleRepository.findByAcademicYearAndBranchCode(academicYear, 0, branchCode);

        // Map to hold our combined results
        Map<String, FeesScheduleChartDTO> summaryMap = new HashMap<>();

        // 1. Process Paid List
        for (FeesScheduleChartProjection p : paidList) {
            FeesScheduleChartDTO dto = summaryMap.computeIfAbsent(p.getMonthName(), FeesScheduleChartDTO::new);
            dto.setPaidAmount(p.getAmount());
        }

        // 2. Process Unpaid List
        for (FeesScheduleChartProjection u : unpaidList) {
            FeesScheduleChartDTO dto = summaryMap.computeIfAbsent(u.getMonthName(), FeesScheduleChartDTO::new);
            dto.setPendingAmount(u.getAmount());
        }
        return new ArrayList<>(summaryMap.values()).stream().sorted((o1, o2) -> o1.getMonthName().compareTo(o2.getMonthName())).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getMonthlyFeesStatus(String role, String email, String month,
                                                    @Nullable String branchCodeFilter) {
        checkPermission(role, email, "Get");

        if ("SUPERADMIN".equalsIgnoreCase(role)) {

            List<String> branchCodes = staffService.getBranchCodesByInstituteEmail(email);

            if (branchCodes == null || branchCodes.isEmpty()) {
                throw new RuntimeException("No branches found for this institute");
            }
            if (branchCodeFilter != null && !branchCodeFilter.isEmpty()) {

                if (!branchCodes.contains(branchCodeFilter)) {
                    throw new RuntimeException("Invalid branchCode for this Superadmin");
                }

                return calculateMonthlyStatusForBranch(month, branchCodeFilter);
            }

            double totalScheduled = 0.0;
            double totalPaid = 0.0;
            double totalPending = 0.0;

            for (String brCode : branchCodes) {
                Map<String, Object> singleBranchData = calculateMonthlyStatusForBranch(month, brCode);

                totalScheduled += (double) singleBranchData.get("totalScheduled");
                totalPaid += (double) singleBranchData.get("totalPaid");
                totalPending += (double) singleBranchData.get("totalPending");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("month", month);
            response.put("totalScheduled", totalScheduled);
            response.put("totalPaid", totalPaid);
            response.put("totalPending", totalPending);

            return response;
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        List<StudentFeeSchedule> schedules = feesScheduleRepository.findByMonthAndBranchCode(month, branchCode);

        double totalScheduled = 0.0;
        double totalPaid = 0.0;
        double totalPending = 0.0;

        for (StudentFeeSchedule schedule : schedules) {
            double scheduleAmount = schedule.getCollectAmount() != null ? schedule.getCollectAmount() : 0.0;
            totalScheduled += scheduleAmount;

            List<StudentFeesCollect> collects = feesCollectRepository.findByScheduleAndBranchCode(schedule, branchCode);
            double paidAmount = collects.stream()
                    .mapToDouble(c -> c.getAmount() != null ? c.getAmount() : 0.0)
                    .sum();

            totalPaid += paidAmount;
            totalPending += (scheduleAmount - paidAmount);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("month", month);
        response.put("totalScheduled", totalScheduled);
        response.put("totalPaid", totalPaid);
        response.put("totalPending", totalPending);

        return response;
    }

    private Map<String, Object> calculateMonthlyStatusForBranch(String month, String branchCode) {

        List<StudentFeeSchedule> schedules = feesScheduleRepository.findByMonthAndBranchCode(month, branchCode);

        double totalScheduled = 0.0;
        double totalPaid = 0.0;
        double totalPending = 0.0;

        for (StudentFeeSchedule schedule : schedules) {
            double scheduleAmount = schedule.getCollectAmount() != null ?
                    schedule.getCollectAmount() : 0.0;
            totalScheduled += scheduleAmount;

            List<StudentFeesCollect> collects = feesCollectRepository.findByScheduleAndBranchCode(schedule, branchCode);
            double paidAmount = collects.stream()
                    .mapToDouble(c -> c.getAmount() != null ? c.getAmount() : 0.0)
                    .sum();

            totalPaid += paidAmount;
            totalPending += (scheduleAmount - paidAmount);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalScheduled", totalScheduled);
        response.put("totalPaid", totalPaid);
        response.put("totalPending", totalPending);

        return response;
    }

    @Override
    public List<ClassFeesRevenueDTO> getClassWiseRevenue(String role, String email) {

        String branchCode =
                staffService.fetchBranchCodeByRole(role, email);

        if (branchCode == null || branchCode.isBlank()) {
            throw new IllegalStateException("Branch code not found");
        }

        List<Object[]> rows =
                feesCollectRepository.findClassWiseRevenueByPaymentMode(branchCode);

        return rows.stream().map(row -> {
            ClassFeesRevenueDTO dto = new ClassFeesRevenueDTO();
            dto.setClassId(row[0] != null ? ((Number) row[0]).longValue() : null);
            dto.setDivision((String) row[1]);
            dto.setStandardName((String) row[2]);
            dto.setDegreeName((String) row[3]);
            dto.setDepartmentName((String) row[4]);
            dto.setPaymentMode((String) row[5]);
            dto.setTotalFeesRevenue(
                    row[6] != null ? ((Number) row[6]).doubleValue() : 0.0
            );
            dto.setStatus("Completed");

            return dto;
        }).toList();
    }


    public StudentFeesDTO mapToDTOFees(StudentFees fees) {
        StudentFeesDTO dto = new StudentFeesDTO();

        dto.setFid(fees.getFid());
        dto.setStudentName(fees.getStudentName());
        dto.setRollNo(fees.getRollNo());
        dto.setStandardName(fees.getStandardName());
        dto.setMediumName(fees.getMediumName());
        dto.setStreamName(fees.getStreamName());
        dto.setGroupName(fees.getGroupName());
        dto.setDegreeName(fees.getDegreeName());
        dto.setDepartmentName(fees.getDepartmentName());
//        dto.setFeesType (fees.getFeesType());
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
        dto.setOtherFees1(fees.getOtherFees1());
        dto.setOtherFees2(fees.getOtherFees2());
        dto.setOtherFees3(fees.getOtherFees3());
//        dto.setLateFeeCharges(fees.getLateFeeCharges());
        dto.setSfid(fees.getSfid());
        dto.setPaidAmount(fees.getPaidAmount());
        dto.setPendingAmount(fees.getPendingAmount());
//        dto.setFeesPaymentType(fees.getFeesPaymentType());
        dto.setInstitutionType(fees.getInstitutionType());
        dto.setStudentId(fees.getStudent().getId());
        dto.setCreatedByEmail(fees.getCreatedByEmail());
        dto.setRole(fees.getRole());
        dto.setBranchCode(fees.getBranchCode());
        dto.setCreatedByEmail(fees.getCreatedByEmail());

        if (StringUtils.isNotBlank(fees.getCreatedByEmail())) {
            try {
                CreatedByResponseDTO creator =
                        staffService.getCreatorByEmail(fees.getCreatedByEmail()).block();

                if (creator != null) {
                    dto.setCreatedByName(creator.getName());
                }
            } catch (Exception ex) {
                dto.setCreatedByName(null);
            }
        }

        if (fees.getScheduleList() != null && !fees.getScheduleList().isEmpty()) {
            List<FeeScheduleDTO> scheduleList = fees.getScheduleList().stream()
                    .map(s -> {
                        FeeScheduleDTO sdto = new FeeScheduleDTO();
                        sdto.setId(s.getId());
                        sdto.setFeesType(s.getFeesType());
                        sdto.setMonth(s.getMonth());
                        sdto.setPaid(s.isPaid());
                        sdto.setCollectAmount(s.getCollectAmount());
                        sdto.setDueDate(s.getDueDate());
                        return sdto;
                    }).collect(Collectors.toList());

            dto.setScheduleList(scheduleList);
        }

        if (!CollectionUtils.isEmpty(dto.getScheduleList())) {
            LocalDate earliestDueDate = dto.getScheduleList().stream().filter(feeScheduleDTO -> !feeScheduleDTO.isPaid())
                    .min(Comparator.comparing(FeeScheduleDTO::getDueDate,
                            Comparator.nullsLast(Comparator.naturalOrder()))).orElse(new FeeScheduleDTO()).getDueDate();

            dto.setDueDate(HelperUtil.getDateWithFormat(earliestDueDate));
        }
        return dto;
    }
}
