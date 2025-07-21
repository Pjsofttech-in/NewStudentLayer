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
    private final GraduationTypeRepository graduationTypeRepository;
    private final ClassRoomService classRoomService;
    private final StaffService staffService;

    @Override
    public StudentPromotionResponseDTO promoteStudent(
            String role, String email, Long studentId,
            Long newStandardId, Long newMediumId,
            Long newDegreeNameId, Long newDepartmentId,
            Long newStreamId, String groupName,
            String academicYear, Long newClassroomId,
            String institutionType, Long graduationTypeId )
    {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to Promote Student");
        }

        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (!"Approved".equalsIgnoreCase(student.getStatus())) {
            throw new RuntimeException("Student cannot be promoted. Status must be 'Approved'.");
        }

        // Step 1: Mark current promotion record as inactive
        promotionRecordRepository.findCurrentByStudentId(studentId).ifPresent(current -> {
            current.setIsCurrent(false);
            promotionRecordRepository.save(current);
        });

        // Step 2: Create and save the previous promotion record
        StudentPromotionRecord record = new StudentPromotionRecord();
        record.setStudent(student);
        record.setAcademicYear(student.getAcademicYear());
        record.setPromotionDate(LocalDate.now());
        record.setIsCurrent(true);
        record.setRollNo(student.getRollNo());
        record.setInstitutionType(student.getInstitutionType());

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
        record.setGroupName(student.getGroupName());
        record.setGraduationType(student.getGraduationType()); // Save previous graduation type
        promotionRecordRepository.save(record);

        // Step 3: Promotion logic based on institutionType
        student.setInstitutionType(institutionType); // update institution type in student

        if ("School".equalsIgnoreCase(institutionType)) {
            StudentStandard standard = standardRepository.findById(newStandardId)
                    .orElseThrow(() -> new RuntimeException("Standard not found"));
            StudentMedium medium = mediumRepository.findById(newMediumId)
                    .orElseThrow(() -> new RuntimeException("Medium not found"));

            student.setStandard(standard);
            student.setStandardName(standard.getStandardName());
            student.setMedium(medium);
            student.setMediumName(medium.getMediumName());

            // Reset college-related fields
            student.setDegreeName(null);
            student.setDepartment(null);
            student.setStream(null);
            student.setStreamName(null);
            student.setGroupName(null);
            student.setGraduationType(null);
        }

        else if ("College".equalsIgnoreCase(institutionType)) {
            // Set new graduation type if provided
            if (graduationTypeId != null) {
                StudentGraduationType graduationType = graduationTypeRepository.findById(graduationTypeId)
                        .orElseThrow(() -> new RuntimeException("GraduationType not found"));
                student.setGraduationType(graduationType);
            }

            // Jr.College promotion logic
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
                student.setGroupName(groupName); // From request

                // Reset UG/PG fields
                student.setDegreeName(null);
                student.setDepartment(null);
            } else {
                // UG/PG promotion logic
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

                // Reset school fields
                student.setStandard(null);
                student.setStandardName(null);
                student.setGroupName(null);  // Not needed in UG/PG
            }
        }

        // Step 4: Update academic year, classroom, and roll number
        student.setAcademicYear(academicYear);
        student.setClassRoom(null);
        student.setRollNo(null);

        studentRepository.save(student);

        // Step 5: Assign new classroom
        classRoomService.assignStudentsToClassroom(role, email, newClassroomId, List.of(student.getId()));

        // Step 6: Reload updated student
        student = studentRepository.findById(studentId).orElseThrow();

        // Step 7: Prepare response
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
        dto.setInstitutionType(record.getInstitutionType());
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

        dto.setGraduationTypeId(record.getGraduationType() != null ? record.getGraduationType().getId() : null);
        dto.setGraduationTypeName(record.getGraduationType() != null ? record.getGraduationType().getGraduationType() : null);

        dto.setGroupName(record.getGroupName());

        return dto;
    }



}
