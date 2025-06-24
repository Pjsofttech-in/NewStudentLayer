package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.PromotionInfoDTO;
import Layer.NewStudentManagement.DTO.StudentPromotionResponseDTO;
import Layer.NewStudentManagement.Entity.StudentEntity;
import Layer.NewStudentManagement.Entity.StudentMedium;
import Layer.NewStudentManagement.Entity.StudentPromotionRecord;
import Layer.NewStudentManagement.Entity.StudentStandard;
import Layer.NewStudentManagement.Repository.MediumRepository;
import Layer.NewStudentManagement.Repository.StandardRepository;
import Layer.NewStudentManagement.Repository.StudentPromotionRepository;
import Layer.NewStudentManagement.Repository.StudentRepository;
import Layer.NewStudentManagement.Service.StudentPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentPromotionServiceImpl implements StudentPromotionService
{
    private final StudentRepository studentRepository;
    private final StandardRepository standardRepository;
    private final MediumRepository mediumRepository;
    private final StudentPromotionRepository promotionRecordRepository;
    private final StaffService staffService;

    @Override
    public StudentPromotionResponseDTO promoteStudent(
            String role, String email,
            Long studentId, Long newStandardId, Long newMediumId, String academicYear
    ) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to Promote Student");
        }

        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));


        promotionRecordRepository.findCurrentByStudentId(studentId).ifPresent(current -> {
            current.setIsCurrent(false);
            promotionRecordRepository.save(current);
        });


        StudentStandard newStandard = standardRepository.findById(newStandardId)
                .orElseThrow(() -> new RuntimeException("Standard not found"));

        StudentMedium newMedium = mediumRepository.findById(newMediumId)
                .orElseThrow(() -> new RuntimeException("Medium not found"));

        StudentPromotionRecord newRecord = new StudentPromotionRecord();
        newRecord.setStudent(student);
        newRecord.setStandard(newStandard);
        newRecord.setMedium(newMedium);
        newRecord.setAcademicYear(academicYear);
        newRecord.setPromotionDate(LocalDate.now());
        newRecord.setIsCurrent(true);

        newRecord.setRollNo(student.getRollNo());
        if (student.getClassRoom() != null) {
            newRecord.setClassroomId(student.getClassRoom().getId());
            if (student.getClassRoom().getDivision() != null) {
                newRecord.setDivision(student.getClassRoom().getDivision().getDivision());
            }
        }

        promotionRecordRepository.save(newRecord);

        // 4. Build response
        StudentPromotionResponseDTO response = new StudentPromotionResponseDTO();
        response.setStudentId(student.getId());
        response.setFullName(student.getFullName());
        response.setRollNo(student.getRollNo());
        response.setBranchCode(student.getBranchCode());

        PromotionInfoDTO current = new PromotionInfoDTO(
                newStandard.getSid(),
                newStandard.getStandardName(),
                newMedium.getMid(),
                newMedium.getMediumName(),
                academicYear,
                newRecord.getPromotionDate(),
                true
        );
        response.setCurrentPromotion(current);

        List<PromotionInfoDTO> history = promotionRecordRepository
                .findAllByStudentIdOrderByPromotionDate(studentId)
                .stream()
                .map(r -> new PromotionInfoDTO(
                        r.getStandard().getSid(),
                        r.getStandard().getStandardName(),
                        r.getMedium().getMid(),
                        r.getMedium().getMediumName(),
                        r.getAcademicYear(),
                        r.getPromotionDate(),
                        r.getIsCurrent()
                ))
                .collect(Collectors.toList());

        response.setPromotionHistory(history);

        return response;
    }


    @Override
    public StudentPromotionResponseDTO getPromotionInfoById(String role, String email, Long studentId) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Get Promote Student");
        }

        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<StudentPromotionRecord> promotions = promotionRecordRepository
                .findAllByStudentIdOrderByPromotionDate(studentId);

        List<PromotionInfoDTO> promotionHistory = promotions.stream()
                .map(this::mapToPromotionInfoDTO)
                .toList();

        PromotionInfoDTO currentPromotion = promotionHistory.stream()
                .filter(PromotionInfoDTO::getIsCurrent)
                .findFirst()
                .orElse(null);

        return new StudentPromotionResponseDTO(
                student.getId(),
                student.getFullName(),
                student.getRollNo(),
                student.getBranchCode(),
                currentPromotion,
                promotionHistory
        );
    }

    private PromotionInfoDTO mapToPromotionInfoDTO(StudentPromotionRecord record) {
        PromotionInfoDTO dto = new PromotionInfoDTO();
        dto.setStandardId(record.getStandard().getSid());
        dto.setStandardName(record.getStandard().getStandardName());
        dto.setMediumId(record.getMedium().getMid());
        dto.setMediumName(record.getMedium().getMediumName());
        dto.setAcademicYear(record.getAcademicYear());
        dto.setPromotionDate(record.getPromotionDate());
        dto.setIsCurrent(record.getIsCurrent());
        return dto;
    }

}
