package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.FeesCollectDTO;
import Layer.NewStudentManagement.Entity.StudentFeeSchedule;
import Layer.NewStudentManagement.Entity.StudentFees;
import Layer.NewStudentManagement.Entity.StudentFeesCollect;
import Layer.NewStudentManagement.Repository.FeesCollectRepository;
import Layer.NewStudentManagement.Repository.FeesRepository;
import Layer.NewStudentManagement.Repository.FeesScheduleRepository;
import Layer.NewStudentManagement.Service.FeesCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeesCollectServiceImpl implements FeesCollectService
{

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

        String collectionType = fees.getFeesCollectionType(); // One Time / Monthly / Installment

        StudentFeeSchedule schedule = null;
        if (!"One Time".equalsIgnoreCase(collectionType)) {
            // Only check schedule for Monthly or Installment
            schedule = feesScheduleRepository
                    .findByStudentFeesAndLabelAndType(fees, collect.getMonth(), collect.getFeesType())
                    .orElseThrow(() -> new RuntimeException("No schedule found for the given Month and FeesType."));

            if (schedule.isPaid()) {
                throw new RuntimeException("Fees already collected for: " + collect.getMonth());
            }

            // Schedule Validations
            LocalDate currentDate = LocalDate.now();

            if ("Monthly".equalsIgnoreCase(schedule.getFeesType())) {
                Month currentMonth = currentDate.getMonth();
                Month scheduledMonth;
                try {
                    scheduledMonth = Month.valueOf(schedule.getMonth().toUpperCase());
                } catch (IllegalArgumentException ex) {
                    throw new RuntimeException("Invalid month format in schedule: " + schedule.getMonth());
                }
                if (currentMonth.getValue() > scheduledMonth.getValue()) {
                    throw new RuntimeException("Cannot pay after the scheduled month: " + scheduledMonth);
                }
            } else if ("Installment".equalsIgnoreCase(schedule.getFeesType())) {
                int scheduledInstallment = extractInstallmentNumber(schedule.getMonth());
                int currentInstallment = extractInstallmentNumber(collect.getMonth());
                if (currentInstallment > scheduledInstallment) {
                    throw new RuntimeException("Cannot pay after the scheduled installment: " + schedule.getMonth());
                }
            }
        }

        // Check duplicate completed payment
        if ("Completed".equalsIgnoreCase(collect.getStatus())) {
            LocalDate now = LocalDate.now();
            LocalDate start = now.withDayOfMonth(1);
            LocalDate end = now.withDayOfMonth(now.lengthOfMonth());

            List<StudentFeesCollect> existing = feesCollectRepository
                    .findCompletedPaymentInMonth(fees.getFid(), start, end);

            for (StudentFeesCollect e : existing) {
                if (e.getMonth().equalsIgnoreCase(collect.getMonth())) {
                    throw new RuntimeException("Fees already collected for this month/installment.");
                }
            }
        }

        // Assign Invoice Number
        Long maxId = feesCollectRepository.findMaxId();
        String invoice = String.format("%06d", (maxId != null ? maxId + 1 : 1));
        collect.setInvoice(invoice);

        // Set metadata
        collect.setCreatedByEmail(email);
        collect.setRole(role);
        collect.setBranchCode(staffService.fetchBranchCodeByRole(role, email));
        collect.setFeesPaymentType(collectionType);
        collect.setPaymentDate(LocalDate.now());

        // Finalize payment
        if ("Completed".equalsIgnoreCase(collect.getStatus())) {
            double newPaidAmount = fees.getPaidAmount() + collect.getAmount();
            double newPending = fees.getTotalamount() - newPaidAmount;

            fees.setPaidAmount(newPaidAmount);
            fees.setPendingAmount(newPending);
            fees.setFeesStatus(newPending <= 0 ? "Completed" : "Ongoing");

            if (schedule != null) {
                schedule.setPaid(true);
                feesScheduleRepository.save(schedule);
            }

            feesRepository.save(fees);
        }

        StudentFeesCollect saved = feesCollectRepository.save(collect);
        return mapToDTO(saved);
    }

    @Override
    public FeesCollectDTO updateFeeCollectionStatus(Long id, String role, String email, String newStatus)
    {
        if(!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to Update Fees Status");
        }
        StudentFeesCollect collect = feesCollectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Collection record not found"));

        if (!collect.getStatus().equalsIgnoreCase(newStatus)) {
            collect.setStatus(newStatus);
            StudentFees fees = collect.getStudentFees();
            if(collect.getPaymentDate()==null || collect.getFeesPaymentType().isEmpty())
            {
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
    public List<FeesCollectDTO> getAllCollectDataByStudentFeesID(Long fid,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to Get Fees Status");
        }

        List<StudentFeesCollect> feesCollects = feesCollectRepository.findAllStudentFeesCollected(fid);

        return feesCollects.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

    }

    @Override
    public List<FeesCollectDTO> getCollectedFeesByStudentId(String role, String email, Long studentId)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to Get Fees Status");
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

        return dto;
    }


}
