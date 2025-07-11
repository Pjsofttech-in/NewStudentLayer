package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.FeesCollectDTO;
import Layer.NewStudentManagement.Entity.StudentFees;
import Layer.NewStudentManagement.Entity.StudentFeesCollect;
import Layer.NewStudentManagement.Repository.FeesCollectRepository;
import Layer.NewStudentManagement.Repository.FeesRepository;
import Layer.NewStudentManagement.Service.FeesCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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



    @Override
    public FeesCollectDTO saveFeeCollection(StudentFeesCollect collect,String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to Collect Fees");
        }
        StudentFees fees = feesRepository.findById(collect.getStudentFees().getFid())
                .orElseThrow(() -> new RuntimeException("Student Fees not found"));

        if("Completed".equalsIgnoreCase(collect.getStatus()))
        {
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
        }
        else
        {
            collect.setPaymentDate(null);
            collect.setDuedate(collect.getDuedate());
        }

        if ("Completed".equalsIgnoreCase(collect.getStatus())) {
            double newPaidAmount = fees.getPaidAmount() + collect.getAmount();
            double newPending = fees.getTotalamount() - newPaidAmount;
            fees.setPaidAmount(newPaidAmount);
            fees.setPendingAmount(newPending);
            fees.setPaymentStatus(newPending <= 0 ? "Completed" : "Ongoing");

            collect.setBranchCode(staffService.fetchBranchCodeByRole(role, email));
            collect.setRole(role);
            collect.setCreatedByEmail(email);
            feesRepository.save(fees);
        }

        collect.setPaymentDate(LocalDate.now());
        StudentFeesCollect feesCollect = feesCollectRepository.save(collect);
        return mapToDTO(feesCollect);
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
                fees.setPaymentStatus(newPending <= 0 ? "Completed" : "Ongoing");
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


    public FeesCollectDTO mapToDTO(StudentFeesCollect feesCollect)
    {
        return new FeesCollectDTO(
                feesCollect.getId(),
                feesCollect.getAmount(),
                feesCollect.getInvoice(),
                feesCollect.getDuedate(),
                feesCollect.getPaymentDate(),
                feesCollect.getPaymentMode(),
                feesCollect.getFeesPaymentType(),
                feesCollect.getStatus()
        );
    }

}
