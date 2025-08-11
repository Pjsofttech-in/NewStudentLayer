package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.TCDTO;
import Layer.NewStudentManagement.Entity.StudentEntity;
import Layer.NewStudentManagement.Entity.StudentTcData;
import Layer.NewStudentManagement.Repository.StudentRepository;
import Layer.NewStudentManagement.Repository.TCDataRepository;
import Layer.NewStudentManagement.Service.TCDataService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TCDataServiceImpl implements TCDataService
{
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TCDataRepository tcDataRepository;

    @Autowired
    StaffService staffService;

    @Override
    public TCDTO generateTc(Long studentId, String role, String email) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to create Student TC");
        }
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        if (Boolean.FALSE.equals(student.isTcGenrated())) {
            throw new RuntimeException("TC is already generated. Apply for duplicate TC.");
        }


        Optional<StudentTcData> existingTcOpt = tcDataRepository.findFirstByStudentIdOrderByTcDateAsc(studentId);

        String tcNumber;
        boolean duplicate;

        if (existingTcOpt.isPresent()) {
            tcNumber = existingTcOpt.get().getTcNumber();
            duplicate = true;
        } else {
            tcNumber = generateTcNumber(branchCode);
            duplicate = false;

            student.setTcGenrated(false);
            studentRepository.save(student);
        }

        StudentTcData newTcData = new StudentTcData();
        newTcData.setTcDate(LocalDate.now()); // new date
        newTcData.setTcNumber(tcNumber);
        newTcData.setDuplicateTc(duplicate);
        newTcData.setStudentEmail(student.getEmail());
        newTcData.setCreatedByEmail(email);
        newTcData.setBranchCode(branchCode);
        newTcData.setRole(role);
        newTcData.setStudent(student);

        StudentTcData savedTc = tcDataRepository.save(newTcData);

        return mapToDto(savedTc);
    }

    @Override
    public List<TCDTO> getAllTCByBranchCode(String role, String email)
    {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Get TC History");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        List<StudentTcData> tcData = tcDataRepository.findAllTCByBranchCode(branchCode);
        return tcData.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

    }

    @Override
    public List<TCDTO> getAllTCByStudentId(Long studentId, String role,String email)
    {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Get TC History");
        }
       List<StudentTcData> tcData = tcDataRepository.findAllByStudentId(studentId);
        return tcData.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

    }


    private String generateTcNumber(String branchCode) {
        int year = LocalDate.now().getYear();
        String prefix = "TC" + year;

        List<String> existingTcNumbers = tcDataRepository
                .findAllTcNumbersByBranchCodeAndYear(branchCode, prefix);

        long max = existingTcNumbers.stream()
                .map(tc -> {
                    try {
                        return Long.parseLong(tc.substring(prefix.length()));
                    } catch (Exception e) {
                        return 0L;
                    }
                })
                .max(Long::compareTo)
                .orElse(0L);

        String newTc;
        do {
            max++;
            newTc = prefix + String.format("%06d", max);
        } while (tcDataRepository.existsByBranchCodeAndTcNumber(branchCode, newTc));

        return newTc;
    }



    public TCDTO mapToDto(StudentTcData entity) {
        TCDTO dto = new TCDTO();
        dto.setId(entity.getId());
        dto.setTcDate(entity.getTcDate());
        dto.setTcNumber(entity.getTcNumber());
        dto.setStudentEmail(entity.getStudentEmail());
        dto.setRole(entity.getRole());
        dto.setCreatedByEmail(entity.getCreatedByEmail());
        dto.setBranchCode(entity.getBranchCode());
        dto.setDuplicateTc(entity.isDuplicateTc());
        dto.setStudentId(entity.getStudent().getId());
        return dto;
    }



}
