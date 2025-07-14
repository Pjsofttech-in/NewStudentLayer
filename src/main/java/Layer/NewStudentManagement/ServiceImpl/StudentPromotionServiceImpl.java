package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.PromotionInfoDTO;
import Layer.NewStudentManagement.DTO.StudentPromotionResponseDTO;
import Layer.NewStudentManagement.Entity.*;
import Layer.NewStudentManagement.Repository.*;
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
    private final DepartmentRepository departmentRepository;
    private final DegreeNameRepository degreeNameRepository;
    private final StaffService staffService;

    @Override
    public StudentPromotionResponseDTO promoteStudent(
            String role, String email,
            Long studentId,
            Long newStandardId,
            Long newMediumId,
            Long newDegreeNameId,
            Long newDepartmentId,
            String academicYear
    ) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to Promote Student");
        }

        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // 1. Mark previous record as inactive
        promotionRecordRepository.findCurrentByStudentId(studentId).ifPresent(current -> {
            current.setIsCurrent(false);
            promotionRecordRepository.save(current);
        });

        // 2. Save promotion history with old data
        StudentPromotionRecord record = new StudentPromotionRecord();
        record.setStudent(student);
        record.setStandard(student.getStandard());
        record.setMedium(student.getMedium());
        record.setAcademicYear(student.getAcademicYear());
        record.setPromotionDate(LocalDate.now());
        record.setIsCurrent(true);
        record.setRollNo(student.getRollNo());

        if (student.getClassRoom() != null) {
            record.setClassroomId(student.getClassRoom().getId());
            if (student.getClassRoom().getDivision() != null) {
                record.setDivision(student.getClassRoom().getDivision().getDivision());
            }
        }

        promotionRecordRepository.save(record);

        // 3. Promote based on Institution Type
        if ("School".equalsIgnoreCase(student.getInstitutionType())) {
            StudentStandard standard = standardRepository.findById(newStandardId)
                    .orElseThrow(() -> new RuntimeException("Standard not found"));

            StudentMedium medium = mediumRepository.findById(newMediumId)
                    .orElseThrow(() -> new RuntimeException("Medium not found"));

            student.setStandard(standard);
            student.setStandardName(standard.getStandardName());
            student.setMedium(medium);
            student.setMediumName(medium.getMediumName());
            student.setDegreeName(null);
            student.setDepartment(null);
        }

        else if ("College".equalsIgnoreCase(student.getInstitutionType())
                && student.getGraduationType() != null
                && "Jr.College".equalsIgnoreCase(student.getGraduationType().getGraduationType())) {

            StudentStandard standard = standardRepository.findById(newStandardId)
                    .orElseThrow(() -> new RuntimeException("Standard not found"));

            StudentMedium medium = mediumRepository.findById(newMediumId)
                    .orElseThrow(() -> new RuntimeException("Medium not found"));

            student.setStandard(standard);
            student.setStandardName(standard.getStandardName());
            student.setMedium(medium);
            student.setMediumName(medium.getMediumName());
            student.setDegreeName(null);
            student.setDepartment(null);
        }

        else if ("College".equalsIgnoreCase(student.getInstitutionType())) {

            StudentDegreeName degree = degreeNameRepository.findById(newDegreeNameId)
                    .orElseThrow(() -> new RuntimeException("DegreeName not found"));
            StudentDepartment department = departmentRepository.findById(newDepartmentId)
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            StudentMedium medium = mediumRepository.findById(newMediumId)
                    .orElseThrow(() -> new RuntimeException("Medium not found"));

            student.setDegreeName(degree);
            student.setDepartment(department);
            student.setMedium(medium);
            student.setMediumName(medium.getMediumName());
            student.setStandard(null);
            student.setStandardName(null);
        }

        // 4. Update common fields
        student.setAcademicYear(academicYear);
        student.setClassRoom(null);
        student.setRollNo(null);

        studentRepository.save(student);

        // 5. Return response
        StudentPromotionResponseDTO response = new StudentPromotionResponseDTO();
        response.setStudentId(student.getId());
        response.setFullName(student.getFullName());
        response.setBranchCode(student.getBranchCode());

        PromotionInfoDTO current = new PromotionInfoDTO(
                student.getStandard() != null ? student.getStandard().getSid() : null,
                student.getStandard() != null ? student.getStandard().getStandardName() : null,
                student.getMedium() != null ? student.getMedium().getMid() : null,
                student.getRollNo() != null ? student.getRollNo():null,
                student.getMedium() != null ? student.getMedium().getMediumName() : null,
                academicYear,
                record.getPromotionDate(),
                true
        );
        response.setCurrentPromotion(current);

        List<PromotionInfoDTO> history = promotionRecordRepository
                .findAllByStudentIdOrderByPromotionDate(studentId)
                .stream()
                .map(r -> new PromotionInfoDTO(
                        r.getStandard() != null ? r.getStandard().getSid() : null,
                        r.getStandard() != null ? r.getStandard().getStandardName() : null,
                        r.getMedium() != null ? r.getMedium().getMid() : null,
                        r.getRollNo() != null ? r.getRollNo():null,
                        r.getMedium() != null ? r.getMedium().getMediumName() : null,
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
        dto.setRollNo(record.getRollNo());
        dto.setMediumName(record.getMedium().getMediumName());
        dto.setAcademicYear(record.getAcademicYear());
        dto.setPromotionDate(record.getPromotionDate());
        dto.setIsCurrent(record.getIsCurrent());
        return dto;
    }

}
