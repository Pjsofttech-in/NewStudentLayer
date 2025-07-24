package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.FeesCollectDTO;
import Layer.NewStudentManagement.DTO.StudentFeeScheduleDTO;
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

            // Validate schedule match
            if (!schedule.getMonth().equalsIgnoreCase(collect.getMonth())) {
                throw new RuntimeException("Schedule month mismatch. Expected: " + schedule.getMonth() + ", Found: " + collect.getMonth());
            }
        }

        // Prevent duplicate collection
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

        // Payment logic if Completed
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

    public FeesCollectDTO getCollectedFeesById(String role, String email, Long id)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to Get Collected Fees by Fees Id");
        }

        StudentFeesCollect collectedFees = feesCollectRepository.findById(id).orElseThrow(()->
                new RuntimeException("FeesCollected Not Found for this Id"));

        return mapToDTO(collectedFees);

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
