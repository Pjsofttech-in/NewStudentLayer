package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.*;
import Layer.NewStudentManagement.Entity.StudentFeeSchedule;
import Layer.NewStudentManagement.Entity.StudentFees;
import Layer.NewStudentManagement.Entity.StudentFeesCollect;
import Layer.NewStudentManagement.Repository.FeesCollectRepository;
import Layer.NewStudentManagement.Repository.FeesRepository;
import Layer.NewStudentManagement.Repository.FeesScheduleRepository;
import Layer.NewStudentManagement.Service.FeesCollectService;
import Layer.NewStudentManagement.Util.HelperUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FeesCollectServiceImpl implements FeesCollectService {

    @Autowired
    FeesCollectRepository feesCollectRepository;

    @Autowired
    FeesRepository feesRepository;

    @Autowired
    StaffService staffService;

    @Autowired
    FeesScheduleRepository feesScheduleRepository;


    @Override
    public FeesCollectDTO saveFeeCollection(StudentFeesCollect collect, String role, String email) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to collect fees.");
        }

        StudentFees fees = feesRepository.findById(collect.getStudentFees().getFid())
                .orElseThrow(() -> new RuntimeException("Student Fees not found"));

        String collectionType = fees.getFeesCollectionType(); // "One Time", "Monthly", "Installment"

        StudentFeeSchedule schedule = null;
        if (!"One Time".equalsIgnoreCase(collectionType)) {
            if (collect.getStudentFeeSchedule() == null || collect.getStudentFeeSchedule().getId() == null) {
                throw new RuntimeException("Schedule ID must be provided for " + collectionType + " type fees.");
            }

            schedule = feesScheduleRepository.findById(collect.getStudentFeeSchedule().getId())
                    .orElseThrow(() -> new RuntimeException("Schedule not found for ID: " + collect.getStudentFeeSchedule().getId()));

            if (!schedule.getStudentFees().getFid().equals(fees.getFid())) {
                throw new RuntimeException("Schedule does not belong to the selected Student Fees.");
            }

            if (schedule.isPaid()) {
                throw new RuntimeException("Fees already collected for schedule: " + schedule.getMonth());
            }

            if (!schedule.getMonth().equalsIgnoreCase(collect.getMonth())) {
                throw new RuntimeException("Schedule month mismatch. Expected: " + schedule.getMonth() + ", Found: " + collect.getMonth());
            }
        }

        if ("Completed".equalsIgnoreCase(collect.getStatus())) {
            List<StudentFeesCollect> alreadyCollected = feesCollectRepository
                    .findCompletedPaymentInMonth(fees.getFid(),
                            LocalDate.now().withDayOfMonth(1),
                            LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));

            for (StudentFeesCollect e : alreadyCollected) {
                if (e.getMonth().equalsIgnoreCase(collect.getMonth())) {
                    throw new RuntimeException("Fees already collected for this month/installment.");
                }
            }
        }

        Long maxId = feesCollectRepository.findMaxId();
        String invoice = String.format("%06d", (maxId != null ? maxId + 1 : 1));
        collect.setInvoice(invoice);

        collect.setCreatedByEmail(email);
        collect.setRole(role);
        collect.setBranchCode(staffService.fetchBranchCodeByRole(role, email));
        collect.setFeesPaymentType(collectionType);
        collect.setPaymentDate(LocalDate.now());

        if ("Completed".equalsIgnoreCase(collect.getStatus())) {
            double newPaidAmount = fees.getPaidAmount() + collect.getAmount();
            double newPending = fees.getTotalamount() - newPaidAmount;

            fees.setPaidAmount(newPaidAmount);
            fees.setPendingAmount(newPending);
            fees.setFeesStatus(newPending <= 0 ? "Completed" : "Ongoing");

            if (schedule != null) {
                schedule.setPaid(true);
                feesScheduleRepository.save(schedule);

                // Check if all schedules are paid
                List<StudentFeeSchedule> allSchedules = feesScheduleRepository.findByStudentFees_Fid(fees.getFid());
                boolean allPaid = allSchedules.stream().allMatch(StudentFeeSchedule::isPaid);

                if (allPaid) {
                    fees.setFeesStatus("Completed");
                } else {
                    fees.setFeesStatus("Ongoing");
                }
            }

            feesRepository.save(fees);
        }

        StudentFeesCollect saved = feesCollectRepository.save(collect);
        return mapToDTO(saved);
    }


    @Override
    public FeesCollectDTO updateFeeCollectionStatus(Long id, String role, String email, String newStatus) {
        if (!staffService.hasPermission(role, email, "Put")) {
            throw new RuntimeException("You don't have permission to Update Fees Status");
        }
        StudentFeesCollect collect = feesCollectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Collection record not found"));

        if (!collect.getStatus().equalsIgnoreCase(newStatus)) {
            collect.setStatus(newStatus);
            StudentFees fees = collect.getStudentFees();
            if (collect.getPaymentDate() == null || collect.getFeesPaymentType().isEmpty()) {
                collect.setPaymentDate(LocalDate.now());
                LocalDate startDate = LocalDate.of(collect.getPaymentDate().getYear(), collect.getPaymentDate().getMonthValue(), 1);
                LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
                List<StudentFeesCollect> alreadyPaid = feesCollectRepository
                        .findCompletedPaymentInMonth(fees.getFid(), startDate, endDate);
                if (!alreadyPaid.isEmpty()) {
                    throw new RuntimeException("Fees already collected with status 'Completed' in this month.");
                }

            }

            if ("Completed".equalsIgnoreCase(newStatus)) {
                double newPaid = fees.getPaidAmount() + collect.getAmount();
                double newPending = fees.getTotalamount() - newPaid;

                fees.setPaidAmount(newPaid);
                fees.setPendingAmount(newPending);
                fees.setFeesStatus(newPending <= 0 ? "Completed" : "Ongoing");
                feesRepository.save(fees);
            }
            StudentFeesCollect fees1 = feesCollectRepository.save(collect);
            return mapToDTO(fees1);
        }

        return mapToDTO(collect);
    }

    @Override
    public List<FeesCollectDTO> getAllCollectDataByStudentFeesID(Long fid, String role, String email) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Get Fees Status");
        }

        List<StudentFeesCollect> feesCollects = feesCollectRepository.findAllStudentFeesCollected(fid);

        return feesCollects.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

    }

    @Override
    public List<FeesCollectDTO> getCollectedFeesByStudentId(String role, String email, Long studentId) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Get Fees by StudentId");
        }
        List<StudentFeesCollect> studentFeesCollects = feesCollectRepository.findCollectedFeesByStudentId(studentId);

        return studentFeesCollects.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private int extractInstallmentNumber(String installmentLabel) {
        // Assumes format like "1st Installment", "2nd Installment"
        String[] parts = installmentLabel.split(" ");
        try {
            return Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            throw new RuntimeException("Invalid installment format: " + installmentLabel);
        }
    }


    @Override
    public FeesCollectDTO getCollectedFeesById(String role, String email, Long id) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Get Collected Fees by Fees Id");
        }

        StudentFeesCollect collectedFees = feesCollectRepository.findById(id).orElseThrow(() ->
                new RuntimeException("FeesCollected Not Found for this Id"));

        return mapToDTO(collectedFees);

    }

    @Override
    public List<Map<String, Object>> getReportByYear(String role, String email, @Nullable String branchCodeFilter) {
        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to Get Collected Fees by Year");
        }

        String finalBranchCode = null;
        List<String> branchCodes = new ArrayList<>();


        if ("SUPERADMIN".equalsIgnoreCase(role)) {

            branchCodes = staffService.getBranchCodesByInstituteEmail(email);

            if (branchCodes == null || branchCodes.isEmpty()) {
                throw new RuntimeException("No branch codes found for this Superadmin");
            }

            if (branchCodeFilter != null && !branchCodeFilter.isEmpty()) {

                if (!branchCodes.contains(branchCodeFilter)) {
                    throw new RuntimeException("Invalid branchCode for this Superadmin");
                }

                List<Object[]> results = feesCollectRepository.getPaidFeesReportByYear(branchCodeFilter);
                return mapYearlyResults(results);
            }

            List<Map<String, Object>> finalResponse = new ArrayList<>();

            for (String brCode : branchCodes) {
                List<Object[]> results = feesCollectRepository.getPaidFeesReportByYear(brCode);

                List<Map<String, Object>> yearlyData = mapYearlyResults(results);

                for (Map<String, Object> map : yearlyData) {
                    map.put("branchCode", brCode);
                    finalResponse.add(map);
                }
            }

            return finalResponse;
        }


        finalBranchCode = staffService.fetchBranchCodeByRole(role, email);

        List<Object[]> results = feesCollectRepository.getPaidFeesReportByYear(finalBranchCode);
        return mapYearlyResults(results);
    }

    private List<Map<String, Object>> mapYearlyResults(List<Object[]> results) {
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {
            Integer year = (Integer) row[0];
            Double total = (Double) row[1];

            String academicYear = year + "-" + (year + 1);

            Map<String, Object> map = new HashMap<>();
            map.put("academicYear", academicYear);
            map.put("totalPaid", total);

            response.add(map);
        }

        return response;
    }

    @Override
    public List<Map<String, Object>> getReportByMonth(String role, String email, int year,
                                                      @Nullable String branchCodeFilter) {

        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Get Collected Fees by Month");
        }

        if ("SUPERADMIN".equalsIgnoreCase(role)) {

            List<String> branchCodes = staffService.getBranchCodesByInstituteEmail(email);

            if (branchCodes == null || branchCodes.isEmpty()) {
                throw new RuntimeException("No branch codes found for this Superadmin");
            }

            if (branchCodeFilter != null && !branchCodeFilter.isEmpty()) {

                if (!branchCodes.contains(branchCodeFilter)) {
                    throw new RuntimeException("Invalid branchCode for this Superadmin");
                }

                return convertMonthlyResults(
                        feesCollectRepository.getPaidFeesReportByMonth(year, branchCodeFilter)
                );
            }
            Map<String, Double> monthlyTotals = new LinkedHashMap<>();

            for (String brCode : branchCodes) {
                List<Object[]> results = feesCollectRepository.getPaidFeesReportByMonth(year, brCode);

                for (Object[] row : results) {
                    String monthName = row[0] != null ? (String) row[0] : "N/A";
                    Double totalPaid = row[1] != null ? (Double) row[1] : 0.0;

                    monthlyTotals.merge(monthName, totalPaid, Double::sum);
                }
            }

            List<Map<String, Object>> finalResponse = new ArrayList<>();

            for (Map.Entry<String, Double> entry : monthlyTotals.entrySet()) {
                String monthName = entry.getKey();
                String shortName = monthName.length() >= 3 ? monthName.substring(0, 3) : monthName;

                Map<String, Object> map = new HashMap<>();
                map.put("month", shortName);
                map.put("totalPaid", entry.getValue());
                finalResponse.add(map);
            }

            return finalResponse;
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        List<Object[]> results = feesCollectRepository.getPaidFeesReportByMonth(year, branchCode);
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {
            String monthName = row[0] != null ? (String) row[0] : "N/A";
            Double totalPaid = row[1] != null ? (Double) row[1] : 0.0;

            String shortName = monthName.length() >= 3 ? monthName.substring(0, 3) : monthName;

            Map<String, Object> map = new HashMap<>();
            map.put("month", shortName);
            map.put("totalPaid", totalPaid);

            response.add(map);
        }

        return response;
    }

    private List<Map<String, Object>> convertMonthlyResults(List<Object[]> results) {
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {
            String monthName = row[0] != null ? (String) row[0] : "N/A";
            Double totalPaid = row[1] != null ? (Double) row[1] : 0.0;

            String shortName = monthName.length() >= 3 ? monthName.substring(0, 3) : monthName;

            Map<String, Object> map = new HashMap<>();
            map.put("month", shortName);
            map.put("totalPaid", totalPaid);

            response.add(map);
        }

        return response;
    }


    @Override
    public List<Map<String, Object>> getReportByStandard(String role, String email) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Get Collected Fees by Standard");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        List<Object[]> results = feesCollectRepository.getPaidFeesReportByStandard(branchCode);
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {
            String standardName = row[0] != null ? (String) row[0] : "N/A"; // handle null
            Integer year = (Integer) row[1];
            Double totalPaid = (Double) row[2];

            Map<String, Object> map = new HashMap<>();
            map.put("standardName", standardName);
            map.put("academicYear", year + "-" + (year + 1));
            map.put("totalPaid", totalPaid);
            response.add(map);
        }
        return response;
    }

    @Override
    public List<FeesByPaymentModeDTO> getCollectedFeesByPaymentMode(
            String role, String email, String branchCode, String institutionType, Integer year) {

        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to Get Collected Fees by Payment Mode");
        }

        String finalBranchCode;

        if ("SUPERADMIN".equalsIgnoreCase(role)) {

            if (branchCode != null && !branchCode.isEmpty()) {
                finalBranchCode = branchCode;
            } else {
                finalBranchCode = null;
            }

        } else {
            finalBranchCode = staffService.fetchBranchCodeByRole(role, email);
        }

        return feesCollectRepository.getCollectedFeesByPaymentMode(
                finalBranchCode,
                institutionType,
                year  // null → all years
        );
    }


    @Override
    public Map<String, Double> getFeesRevenueByBank(String role, String email, @Nullable String branchCodeFilter) {

        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Get Collected Fees by BankName");
        }

        if ("SUPERADMIN".equalsIgnoreCase(role)) {

            List<String> branchCodes = staffService.getBranchCodesByInstituteEmail(email);

            if (branchCodes == null || branchCodes.isEmpty()) {
                throw new RuntimeException("No branch codes found for this Superadmin");
            }

            if (branchCodeFilter != null && !branchCodeFilter.isEmpty()) {

                if (!branchCodes.contains(branchCodeFilter)) {
                    throw new RuntimeException("Invalid branchCode for this Superadmin");
                }

                return convertBankRevenue(
                        feesCollectRepository.getFeesRevenueByBank(branchCodeFilter)
                );
            }

            Map<String, Double> combinedMap = new LinkedHashMap<>();

            for (String brCode : branchCodes) {

                List<Object[]> results = feesCollectRepository.getFeesRevenueByBank(brCode);

                for (Object[] row : results) {

                    String bankName = row[0] != null ? row[0].toString() : "Unknown";
                    Double amount = ((Number) row[1]).doubleValue();

                    combinedMap.merge(bankName, amount, Double::sum);
                }
            }

            return combinedMap;
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        List<Object[]> results = feesCollectRepository.getFeesRevenueByBank(branchCode);

        Map<String, Double> revenueMap = new LinkedHashMap<>();
        for (Object[] row : results) {
            String bankName = row[0] != null ? row[0].toString() : "Unknown";
            Double amount = ((Number) row[1]).doubleValue();
            revenueMap.put(bankName, amount);
        }

        return revenueMap;
    }

    private Map<String, Double> convertBankRevenue(List<Object[]> results) {
        Map<String, Double> revenueMap = new LinkedHashMap<>();

        for (Object[] row : results) {
            String bankName = row[0] != null ? row[0].toString() : "Unknown";
            Double amount = ((Number) row[1]).doubleValue();
            revenueMap.put(bankName, amount);
        }

        return revenueMap;
    }


    @Override
    public Page<StudentFeesHistoryDTO> getAllCollectedFeesByBranch(
            String role, String email,
            FeesFilterDTO filterDTO,
            String timeFrame,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size,
            String sort) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Get Collected Fees History");
        }
        String[] arr = sort.split(",");
        Sort.Direction dir = "DESC".equalsIgnoreCase(arr[1]) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, arr[0]));

        LocalDate today = LocalDate.now();
        LocalDate fromDate = null;
        LocalDate toDate = null;

        switch (timeFrame != null ? timeFrame.toLowerCase() : "all") {
            case "today" -> {
                fromDate = today;
                toDate = today;
            }
            case "7days", "last7days" -> {
                fromDate = today.minusDays(7);
                toDate = today;
            }
            case "30days", "last30days" -> {
                fromDate = today.minusDays(30);
                toDate = today;
            }
            case "365days", "year", "last365days" -> {
                fromDate = today.minusDays(365);
                toDate = today;
            }
            case "custom" -> {
                if (startDate == null || endDate == null)
                    throw new RuntimeException("Start date and end date are required for custom range");
                fromDate = startDate;
                toDate = endDate;
            }
            default -> {
                fromDate = null; // all
                toDate = null;
            }
        }

        final LocalDate finalFromDate = fromDate;
        final LocalDate finalToDate = toDate;

        Page<StudentFees> studentFeesPage = feesRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("branchCode"), branchCode));

            if (filterDTO != null) {

                if (filterDTO.getStudentName() != null && !filterDTO.getStudentName().isEmpty())
                    predicates.add(cb.like(root.get("studentName"), filterDTO.getStudentName()));

                if (filterDTO.getStandardName() != null && !filterDTO.getStandardName().isEmpty())
                    predicates.add(cb.equal(root.get("standardName"), filterDTO.getStandardName()));

                if (filterDTO.getMediumName() != null && !filterDTO.getMediumName().isEmpty())
                    predicates.add(cb.equal(root.get("mediumName"), filterDTO.getMediumName()));

                if (filterDTO.getStreamName() != null && !filterDTO.getStreamName().isEmpty())
                    predicates.add(cb.equal(root.get("streamName"), filterDTO.getStreamName()));

                if (filterDTO.getGroupName() != null && !filterDTO.getGroupName().isEmpty())
                    predicates.add(cb.equal(root.get("groupName"), filterDTO.getGroupName()));

                if (filterDTO.getDegreeName() != null && !filterDTO.getDegreeName().isEmpty())
                    predicates.add(cb.equal(root.get("degreeName"), filterDTO.getDegreeName()));

                if (filterDTO.getDepartmentName() != null && !filterDTO.getDepartmentName().isEmpty())
                    predicates.add(cb.equal(root.get("departmentName"), filterDTO.getDepartmentName()));

                if (filterDTO.getInstitutionType() != null && !filterDTO.getInstitutionType().isEmpty())
                    predicates.add(cb.equal(root.get("institutionType"), filterDTO.getInstitutionType()));

                if (filterDTO.getFeesCollectionType() != null && !filterDTO.getFeesCollectionType().isEmpty())
                    predicates.add(cb.equal(root.get("feesCollectionType"), filterDTO.getFeesCollectionType()));

                if (filterDTO.getFeesStatus() != null && !filterDTO.getFeesStatus().isEmpty())
                    predicates.add(cb.equal(root.get("feesStatus"), filterDTO.getFeesStatus()));

                if (StringUtils.isNotBlank(filterDTO.getCreatedByEmail()))
                    predicates.add(cb.equal(root.get("createdByEmail"), filterDTO.getCreatedByEmail()));

                if (StringUtils.isNotBlank(filterDTO.getCreatedByName())) {
                    CreatedByResponseDTO response = staffService.getCreatorByName(filterDTO.getCreatedByName()).block();
                    String creatorEmail = Objects.isNull(response) ? "null" : response.getName();
                    predicates.add(cb.equal(root.get("createdByEmail"), creatorEmail));
                }

                if (StringUtils.isNotBlank(filterDTO.getDueDate()) && HelperUtil.isStrictlyValidDate(filterDTO.getDueDate())) {
                    LocalDate parsedDate = HelperUtil.parseDateWithFormat(filterDTO.getDueDate());

                    Subquery<Long> subquery = null;
                    subquery = query.subquery(Long.class);
                    Root<StudentFeeSchedule> childRoot = subquery.from(StudentFeeSchedule.class);

                    // 2. Define the link between Parent and Child
                    // Assuming your ChildEntity has a field named 'mainObject' that links back to the parent
                    Predicate parentLink = cb.equal(childRoot.get("studentFees"), root);

                    // 3. Define your nested filters
                    Predicate isUnpaid = cb.equal(childRoot.get("isPaid"), false);
                    Predicate hasDate = cb.isNotNull(childRoot.get("dueDate"));
                    Predicate isBefore = cb.lessThanOrEqualTo(childRoot.get("dueDate"), parsedDate);

                    // 4. Configure the subquery to select IDs where conditions match
                    subquery.select(childRoot.get("id"))
                            .where(parentLink, isUnpaid, hasDate, isBefore);
                    predicates.add(cb.exists(subquery));
                }
            }

            if (finalFromDate != null && finalToDate != null) {
                predicates.add(cb.between(root.get("approvalDate"), finalFromDate, finalToDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        List<StudentFeesHistoryDTO> result = new ArrayList<>();

        for (StudentFees sf : studentFeesPage.getContent()) {
            List<StudentFeesCollect> collections =
                    feesCollectRepository.findAllStudentFeesCollected(sf.getFid());

            List<FeesCollectionDetailDTO> collectionHistory = collections.stream()
                    .filter(c -> {
                        if (finalFromDate != null && finalToDate != null && c.getPaymentDate() != null) {
                            return !c.getPaymentDate().isBefore(finalFromDate) &&
                                    !c.getPaymentDate().isAfter(finalToDate);
                        }
                        return true;
                    })
                    .map(c -> new FeesCollectionDetailDTO(
                            c.getAmount(),
                            c.getPaymentDate(),
                            c.getPaymentMode(),
                            c.getFeesType(),
                            c.getMonth(),
                            c.getTransactionId()
                    ))
                    .collect(Collectors.toList());

            StudentFeesHistoryDTO dto = new StudentFeesHistoryDTO();
            dto.setStudentId(sf.getStudent().getId());
            dto.setStudentName(sf.getStudentName());
            dto.setRollNo(sf.getRollNo());
            dto.setStandardName(sf.getStandardName());
            dto.setMediumName(sf.getMediumName());
            dto.setStreamName(sf.getStreamName());
            dto.setGroupName(sf.getGroupName());
            dto.setDegreeName(sf.getDegreeName());
            dto.setDepartmentName(sf.getDepartmentName());
            dto.setInstitutionType(sf.getInstitutionType());
            dto.setBranchCode(sf.getBranchCode());
            dto.setTotalPaidAmount(sf.getPaidAmount());
            dto.setPendingAmount(sf.getPendingAmount());
            dto.setFeesCollectionType(sf.getFeesCollectionType());
            dto.setPaymentHistory(collectionHistory);
            dto.setCreatedByEmail(sf.getCreatedByEmail());

            if (StringUtils.isNotBlank(sf.getCreatedByEmail())) {
                try {
                    CreatedByResponseDTO creator =
                            staffService.getCreatorByEmail(sf.getCreatedByEmail()).block();

                    if (creator != null) {
                        dto.setCreatedByName(creator.getName());
                        dto.setRole(creator.getType());
                    }
                } catch (Exception ex) {
                    dto.setCreatedByName(null);
                }
            }

            if(!CollectionUtils.isEmpty(sf.getScheduleList())) {
                LocalDate earliestDueDate = sf.getScheduleList().stream().filter(s -> !s.isPaid())
                        .min(Comparator.comparing(StudentFeeSchedule::getDueDate,
                                Comparator.nullsLast(Comparator.naturalOrder()))).orElse(new StudentFeeSchedule()).getDueDate();

                dto.setDueDate(HelperUtil.getDateWithFormat(earliestDueDate));
            }

            result.add(dto);
        }

        return new PageImpl<>(result, pageable, studentFeesPage.getTotalElements());
    }


    @Override
    public Map<String, Object> getDailyCollectedFees(String role, String email, LocalDate date) {

        if (date == null) {
            date = LocalDate.now();
        }

        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Get Collected Fees History");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        Double totalCollected = feesCollectRepository
                .getTotalFeesByDateAndBranch(date, branchCode);

        if (totalCollected == null) {
            totalCollected = 0.0;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("date", date);
        response.put("totalCollectedFees", totalCollected);

        return response;
    }

    @Override
    public Map<String, Object> getCollectedFeesByFilter(String role, String email, String filter,
                                                        LocalDate fromDate, LocalDate toDate) {

        // ✅ 1. Permission Check
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("No permission");
        }

        // ✅ 2. BranchCode
        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        // ✅ 3. Date Range Logic
        LocalDate startDate;
        LocalDate endDate = LocalDate.now();

        switch (filter.toLowerCase()) {

            case "today":
                startDate = LocalDate.now();
                break;

            case "7days":
                startDate = LocalDate.now().minusDays(7);
                break;

            case "30days":
                startDate = LocalDate.now().minusDays(30);
                break;

            case "last365days":
                startDate = LocalDate.now().minusDays(365);
                break;

            case "custom":
                if (fromDate == null || toDate == null) {
                    throw new RuntimeException("Custom filter requires fromDate and toDate");
                }
                startDate = fromDate;
                endDate = toDate;
                break;

            default:
                throw new RuntimeException("Invalid filter");
        }

        // ✅ 4. Fetch Data
        List<StudentFeesCollect> data =
                feesCollectRepository.findByBranchAndDateRange(branchCode, startDate, endDate);

        // ✅ 5. Role-based Filtering
        List<StudentFeesCollect> filteredData = new ArrayList<>();

        for (StudentFeesCollect fee : data) {

            if ("BRANCH".equalsIgnoreCase(role)) {
                filteredData.add(fee); // all

            } else if ("DEPARTMENT".equalsIgnoreCase(role)) {
                // 👉 if you add department field, filter here
                filteredData.add(fee);

            } else if ("STAFF".equalsIgnoreCase(role)) {
                if (fee.getCreatedByEmail().equalsIgnoreCase(email)) {
                    filteredData.add(fee);
                }
            }
        }

        // ✅ 6. Total Calculation
        double total = filteredData.stream()
                .mapToDouble(StudentFeesCollect::getAmount)
                .sum();

        // ✅ 7. Response
        Map<String, Object> response = new HashMap<>();
        response.put("filter", filter);
        response.put("totalCollected", total);
        response.put("totalRecords", filteredData.size());
        response.put("data", filteredData);

        return response;
    }


    private FeesCollectDTO mapToDTO(StudentFeesCollect feesCollect) {
        FeesCollectDTO dto = new FeesCollectDTO();
        dto.setId(feesCollect.getId());
        dto.setAmount(feesCollect.getAmount());
        dto.setInvoice(feesCollect.getInvoice());
        dto.setDuedate(feesCollect.getDuedate());
        dto.setPaymentDate(feesCollect.getPaymentDate());
        dto.setPaymentMode(feesCollect.getPaymentMode());
        dto.setFeesPaymentType(feesCollect.getFeesPaymentType());
        dto.setStatus(feesCollect.getStatus());

        dto.setTuitionFee(feesCollect.getTuitionFee());
        dto.setAdmissionFee(feesCollect.getAdmissionFee());
        dto.setPracticalFee(feesCollect.getPracticalFee());
        dto.setComputerClassFee(feesCollect.getComputerClassFee());
        dto.setExamFees(feesCollect.getExamFees());
        dto.setUniformFee(feesCollect.getUniformFee());
        dto.setTransportBusFee(feesCollect.getTransportBusFee());
        dto.setHostelFee(feesCollect.getHostelFee());
        dto.setBuildingFundFee(feesCollect.getBuildingFundFee());
        dto.setLibraryFees(feesCollect.getLibraryFees());
        dto.setSportFees(feesCollect.getSportFees());
        dto.setTransactionId(feesCollect.getTransactionId());
        dto.setBankName(feesCollect.getBankName());
        dto.setBankBranchName(feesCollect.getBankBranchName());
        dto.setIfscCode(feesCollect.getIfscCode());
        dto.setAccountHolderName(feesCollect.getAccountHolderName());

        dto.setStudentFeesId(
                feesCollect.getStudentFees() != null ? feesCollect.getStudentFees().getFid() : null
        );

        if (feesCollect.getStudentFeeSchedule() != null) {
            StudentFeeSchedule schedule = feesCollect.getStudentFeeSchedule();
            StudentFeeScheduleDTO scheduleDTO = new StudentFeeScheduleDTO();
            scheduleDTO.setId(schedule.getId());
            scheduleDTO.setMonth(schedule.getMonth());
            scheduleDTO.setPaid(schedule.isPaid());
            scheduleDTO.setDueDate(schedule.getDueDate());
            scheduleDTO.setAmount(schedule.getCollectAmount());
            scheduleDTO.setFeesType(schedule.getFeesType());
            dto.setSchedule(scheduleDTO);
        }

        return dto;
    }
}
