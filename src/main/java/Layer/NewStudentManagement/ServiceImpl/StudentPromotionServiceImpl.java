package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.PromotionInfoDTO;
import Layer.NewStudentManagement.DTO.StudentPromotionResponseDTO;
import Layer.NewStudentManagement.Entity.*;
import Layer.NewStudentManagement.Repository.*;
import Layer.NewStudentManagement.Service.ClassRoomService;
import Layer.NewStudentManagement.Service.StudentPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private final StreamRepository streamRepository;
    private final GroupRepository groupRepository;
    private final ClassRoomService classRoomService;
    private final StaffService staffService;

    @Override
    public StudentPromotionResponseDTO promoteStudent(
            String role, String email, Long studentId,
            Long newStandardId, Long newMediumId,
            Long newDegreeNameId, Long newDepartmentId,
            Long newStreamId, String groupName, // <-- groupName as String
            String academicYear, Long newClassroomId) {

        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to Promote Student");
        }

        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // 1. Mark current promotion record inactive
        promotionRecordRepository.findCurrentByStudentId(studentId).ifPresent(current -> {
            current.setIsCurrent(false);
            promotionRecordRepository.save(current);
        });

        // 2. Create promotion record with previous data
        StudentPromotionRecord record = new StudentPromotionRecord();
        record.setStudent(student);
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

        record.setStandard(student.getStandard());
        record.setStandardName(student.getStandardName());

        record.setMedium(student.getMedium());
        record.setMediumName(student.getMediumName());

        record.setDegree(student.getDegreeName());
        record.setDepartment(student.getDepartment());

        record.setStream(student.getStream());
        record.setStreamName(student.getStreamName());

        // Set previous groupName string in promotion record
        record.setGroupName(student.getGroupName());

        promotionRecordRepository.save(record);

        // 3. Promotion logic
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
            student.setStream(null);
            student.setStreamName(null);
            student.setGroupName(null);  // reset groupName
        }

        else if ("College".equalsIgnoreCase(student.getInstitutionType())) {
            if (student.getGraduationType() != null &&
                    "Jr.College".equalsIgnoreCase(student.getGraduationType().getGraduationType())) {

                StudentStandard standard = standardRepository.findById(newStandardId)
                        .orElseThrow(() -> new RuntimeException("Standard not found"));
                StudentMedium medium = mediumRepository.findById(newMediumId)
                        .orElseThrow(() -> new RuntimeException("Medium not found"));
                StudentStream stream = streamRepository.findById(newStreamId)
                        .orElseThrow(() -> new RuntimeException("Stream not found"));

                student.setStandard(standard);
                student.setStandardName(standard.getStandardName());

                student.setMedium(medium);
                student.setMediumName(medium.getMediumName());

                student.setStream(stream);
                student.setStreamName(stream.getStream());

                // Set the passed groupName string directly
                student.setGroupName(groupName);

                student.setDegreeName(null);
                student.setDepartment(null);

            } else {
                // UG/PG Promotion
                StudentDegreeName degree = degreeNameRepository.findById(newDegreeNameId)
                        .orElseThrow(() -> new RuntimeException("DegreeName not found"));
                StudentDepartment department = departmentRepository.findById(newDepartmentId)
                        .orElseThrow(() -> new RuntimeException("Department not found"));
                StudentMedium medium = mediumRepository.findById(newMediumId)
                        .orElseThrow(() -> new RuntimeException("Medium not found"));
                StudentStream stream = streamRepository.findById(newStreamId)
                        .orElseThrow(() -> new RuntimeException("Stream not found"));

                student.setDegreeName(degree);
                student.setDepartment(department);

                student.setMedium(medium);
                student.setMediumName(medium.getMediumName());

                student.setStream(stream);
                student.setStreamName(stream.getStream());

                student.setStandard(null);
                student.setStandardName(null);
                student.setGroupName(null);  // reset groupName for UG/PG
            }
        }

        // 4. Update academic year and reset classroom
        student.setAcademicYear(academicYear);
        student.setClassRoom(null);
        student.setRollNo(null);
        studentRepository.save(student);

        // 5. Assign new classroom and generate roll number
        classRoomService.assignStudentsToClassroom(role, email, newClassroomId, List.of(student.getId()));

        // 6. Reload updated student
        student = studentRepository.findById(studentId).orElseThrow();

        // 7. Build response
        StudentPromotionResponseDTO response = new StudentPromotionResponseDTO();
        response.setStudentId(student.getId());
        response.setFullName(student.getFullName());
        response.setBranchCode(student.getBranchCode());


        StudentPromotionRecord latestRecord = promotionRecordRepository.findCurrentByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Current promotion not found"));
        PromotionInfoDTO current = mapToPromotionInfoDTO(latestRecord);
        response.setCurrentPromotion(current);

        List<PromotionInfoDTO> history = promotionRecordRepository
                .findAllByStudentIdOrderByPromotionDate(studentId)
                .stream()
                .map(this::mapToPromotionInfoDTO)
                .collect(Collectors.toList());

        response.setPromotionHistory(history);


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

        dto.setStandardId(record.getStandard() != null ? record.getStandard().getSid() : null);
        dto.setStandardName(record.getStandardName());

        dto.setMediumId(record.getMedium() != null ? record.getMedium().getMid() : null);
        dto.setMediumName(record.getMediumName());

        dto.setRollNo(record.getRollNo());
        dto.setAcademicYear(record.getAcademicYear());
        dto.setPromotionDate(record.getPromotionDate());
        dto.setIsCurrent(record.getIsCurrent());

        dto.setClassroomId(record.getClassroomId());
        dto.setDivision(record.getDivision());

        dto.setStudentId(record.getStudent() != null ? record.getStudent().getId() : null);
        dto.setStudentFullName(record.getStudent() != null ? record.getStudent().getFullName() : null);

        // Newly added fields
        dto.setDegreeId(record.getDegree() != null ? record.getDegree().getId() : null);
        dto.setDegreeName(record.getDegree() != null ? record.getDegree().getDegreeName() : null);

        dto.setDepartmentId(record.getDepartment() != null ? record.getDepartment().getId() : null);
        dto.setDepartmentName(record.getDepartment() != null ? record.getDepartment().getDepartmentName() : null);

        dto.setStreamId(record.getStream() != null ? record.getStream().getId() : null);
        dto.setStreamName(record.getStreamName());

        dto.setGroupName(record.getGroupName());

        return dto;
    }



}
