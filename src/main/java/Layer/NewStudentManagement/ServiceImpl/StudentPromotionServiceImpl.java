package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.PromotionInfoDTO;
import Layer.NewStudentManagement.DTO.StudentBulkPromotionResponseDTO;
import Layer.NewStudentManagement.DTO.StudentPromotionResponseDTO;
import Layer.NewStudentManagement.Entity.*;
import Layer.NewStudentManagement.Repository.*;
import Layer.NewStudentManagement.Service.ClassRoomService;
import Layer.NewStudentManagement.Service.StudentPromotionService;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionTemplate;
import software.amazon.awssdk.utils.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentPromotionServiceImpl implements StudentPromotionService
{
    private final StudentRepository studentRepository;
    private final StandardRepository standardRepository;
    private final MediumRepository mediumRepository;
    private final StudentPromotionRepository promotionRecordRepository;
    private final DegreeNameRepository degreeNameRepository;
    private final StreamRepository streamRepository;
    private final GraduationTypeRepository graduationTypeRepository;
    private final ClassRoomService classRoomService;
    private final StaffService staffService;
    private final PlatformTransactionManager transactionManager;
    private final CourseTypeRepository courseTypeRepository;


    @Override
    public StudentPromotionResponseDTO promoteStudent(
            String role, String email, Long studentId,
            Long newStandardId, Long newMediumId,
            Long newDegreeNameId, String newDepartmentName,
            Long newStreamId, String groupName,
            String academicYear, Long newClassroomId,
            String institutionType, Long graduationTypeId, Long courseTypeId) {
        try {
            if (!staffService.hasPermission(role, email, "Post")) {
                throw new RuntimeException("You don't have permission to Promote Student");
            }

            StudentEntity student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            if (!"Approved".equalsIgnoreCase(student.getStatus())) {
                throw new RuntimeException("Student cannot be promoted. Status must be 'Approved'.");
            }

            Long sid = null;
            if (student.getStandard() != null) {
                sid = student.getStandard().getSid();
            }
            // Step 1: Save current record as previous promotion
            Optional<StudentPromotionRecord> previousOpt = promotionRecordRepository.findCurrentByStudentIdStandardId(studentId, sid, student.getDepartmentName());
            StudentPromotionRecord previous = previousOpt.orElse(new StudentPromotionRecord());
            previous.setStudent(student);
            previous.setAcademicYear(student.getAcademicYear());
            previous.setPromotionDate(LocalDate.now());
            previous.setIsCurrent(false); // Mark as old
            previous.setRollNo(student.getRollNo());
            previous.setInstitutionType(student.getInstitutionType());

            if (student.getClassRoom() != null) {
                previous.setClassroomId(student.getClassRoom().getId());
                if (student.getClassRoom().getDivision() != null) {
                    previous.setDivision(student.getClassRoom().getDivision().getDivision());
                }
            }

            previous.setStandard(student.getStandard());
            previous.setStandardName(student.getStandard().getStandardName());
            previous.setMedium(student.getMedium());
            previous.setMediumName(student.getMediumName());
            previous.setDegree(student.getDegreeName());
            previous.setDepartmentName(student.getDepartmentName());
            previous.setStream(student.getStream());
            previous.setStreamName(student.getStreamName());
            previous.setGroupName(student.getGroupName());
            previous.setGraduationType(student.getGraduationType());

            promotionRecordRepository.save(previous); // Save previous record

            // Step 2: Apply promotion
            student.setInstitutionType(institutionType);

            if ("School".equalsIgnoreCase(institutionType)) {
                StudentStandard standard = standardRepository.findById(newStandardId)
                        .orElseThrow(() -> new RuntimeException("Standard not found"));
                StudentMedium medium = mediumRepository.findById(newMediumId)
                        .orElseThrow(() -> new RuntimeException("Medium not found"));

                student.setStandard(standard);
                student.setStandardName(standard.getStandardName());
                student.setMedium(medium);
                student.setMediumName(medium.getMediumName());

                // Reset college fields
                student.setDegreeName(null);
                student.setDepartmentName(null);
                student.setStream(null);
                student.setStreamName(null);
                student.setGroupName(null);
                student.setGraduationType(null);
            } else if ("College".equalsIgnoreCase(institutionType)) {
                if (graduationTypeId != null) {
                    StudentGraduationType graduationType = graduationTypeRepository.findById(graduationTypeId)
                            .orElseThrow(() -> new RuntimeException("GraduationType not found"));
                    student.setGraduationType(graduationType);
                }

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
                    student.setGroupName(groupName);

                    student.setDegreeName(null);
                    student.setDepartmentName(null);
                } else if (student.getGraduationType() != null &&
                        "Diploma".equalsIgnoreCase(student.getGraduationType().getGraduationType())) {

                    if (courseTypeId != null) {
                        StudentCourseType courseType = courseTypeRepository.findById(courseTypeId)
                                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseTypeId));
                        student.setCourseType(courseType);
                    }

                    if (newMediumId != null) {
                        StudentMedium medium = mediumRepository.findById(newMediumId)
                                .orElseThrow(() -> new RuntimeException("Medium not found with ID: " + newMediumId));
                        student.setMedium(medium);
                        student.setMediumName(medium.getMediumName());
                    }

                    if (StringUtils.isBlank(newDepartmentName)) {
                        throw new RuntimeException("Department can not be null");
                    }
                    student.setDepartmentName(newDepartmentName);


                    student.setGroupName(null);
                    student.setStandardName(null);
                    student.setStandard(null);
                } else {
                    StudentDegreeName degree = degreeNameRepository.findById(newDegreeNameId)
                            .orElseThrow(() -> new RuntimeException("DegreeName not found"));
                    StudentMedium medium = mediumRepository.findById(newMediumId)
                            .orElseThrow(() -> new RuntimeException("Medium not found"));
                    StudentStream stream = streamRepository.findById(newStreamId)
                            .orElseThrow(() -> new RuntimeException("Stream not found"));

                    student.setDegreeName(degree);
                    student.setMedium(medium);
                    student.setMediumName(medium.getMediumName());
                    student.setStream(stream);
                    student.setStreamName(stream.getStream());

                    student.setStandard(null);
                    student.setStandardName(null);
                    student.setGroupName(null);
                }
            }

            // Step 3: Update academic year, classroom, and roll number
            student.setAcademicYear(academicYear);
            student.setClassRoom(null);
            student.setRollNo(null);

            studentRepository.save(student);

            // Step 4: Assign to classroom
            classRoomService.assignStudentsToClassroom(role, email, newClassroomId, List.of(student.getId()));

            // Step 5: Save new promotion record (after update)
            StudentPromotionRecord current = new StudentPromotionRecord();
            current.setStudent(student);
            current.setAcademicYear(student.getAcademicYear());
            current.setPromotionDate(LocalDate.now());
            current.setIsCurrent(true); // Mark this one as latest
            current.setRollNo(student.getRollNo());
            current.setInstitutionType(student.getInstitutionType());

            if (student.getClassRoom() != null) {
                current.setClassroomId(student.getClassRoom().getId());
                if (student.getClassRoom().getDivision() != null) {
                    current.setDivision(student.getClassRoom().getDivision().getDivision());
                }
            }

            current.setStandard(student.getStandard());
            current.setStandardName(student.getStandardName());
            current.setMedium(student.getMedium());
            current.setMediumName(student.getMediumName());
            current.setDegree(student.getDegreeName());
            current.setDepartmentName(student.getDepartmentName());
            current.setStream(student.getStream());
            current.setStreamName(student.getStreamName());
            current.setGroupName(student.getGroupName());
            current.setGraduationType(student.getGraduationType());

            promotionRecordRepository.save(current);

            // Step 6: Prepare response
            List<StudentPromotionRecord> allPromotions = promotionRecordRepository.findAllByStudentIdOrderByPromotionDate(studentId);

            StudentPromotionResponseDTO response = new StudentPromotionResponseDTO();
            response.setStudentId(student.getId());
            response.setFullName(student.getFullName());
            response.setBranchCode(student.getBranchCode());

            PromotionInfoDTO currentDTO = mapToPromotionInfoDTO(allPromotions.get(0));
            currentDTO.setIsCurrent(true);
            response.setCurrentPromotion(currentDTO);

            List<PromotionInfoDTO> history = allPromotions.stream()
                    .skip(1)
                    .map(this::mapToPromotionInfoDTO)
                    .peek(p -> p.setIsCurrent(false))
                    .collect(Collectors.toList());

            response.setPromotionHistory(history);
            return response;
        } catch (Exception e) {
            log.error("exception in promoteStudent() in StudentPromotionServiceImpl", e);
            throw e;
        }
    }

    public StudentBulkPromotionResponseDTO promoteStudentList(
            String role, String email, List<Long> studentIdList,
            Long newStandardId, Long newMediumId,
            Long newDegreeNameId, String newDepartmentName,
            Long newStreamId, String groupName,
            String academicYear, Long newClassroomId,
            String institutionType, Long graduationTypeId, Long courseTypeId) {
        StudentBulkPromotionResponseDTO response = new StudentBulkPromotionResponseDTO();
        response.setStatus("Failed");
        try {
            if (CollectionUtils.isNullOrEmpty(studentIdList)) {
                throw new RuntimeException("Input Student Id List cannot be empty");
            } else {
                List<Long> failedStudentIds = new ArrayList<>();
                List<StudentPromotionResponseDTO> successfulStudentList = new ArrayList<>();
                TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
                studentIdList.forEach(studentId -> {
                    transactionTemplate.executeWithoutResult(status -> {
                        // Database operations live here
                        try {
                            StudentPromotionResponseDTO studentPromotionResponseDTO = promoteStudent(role, email, studentId, newStandardId, newMediumId, newDegreeNameId, newDepartmentName, newStreamId, groupName,
                                    academicYear, newClassroomId, institutionType, graduationTypeId, courseTypeId);
                            successfulStudentList.add(studentPromotionResponseDTO);
                        } catch (Exception e) {
                            failedStudentIds.add(studentId);
                            status.setRollbackOnly();
                        }
                    });
                });
                response.setFailedStudentIds(failedStudentIds);
                response.setSuccessfulStudentList(successfulStudentList);
                if (CollectionUtils.isNullOrEmpty(failedStudentIds) && !CollectionUtils.isNullOrEmpty(successfulStudentList)) {
                    response.setStatus("Success");
                } else if (CollectionUtils.isNullOrEmpty(successfulStudentList)) {
                    response.setStatus("Failure");
                } else {
                    response.setStatus("PartialSuccess");
                }
            }
        } catch (Exception e) {
            log.error("exception in promoteStudent() in StudentPromotionServiceImpl", e);
            throw e;
        }
        return response;
    }

    @Override
    public StudentPromotionResponseDTO getPromotionInfoById(String role, String email, Long studentId) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Get Promote Student");
        }

        // Fetch student data
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Get promotion records for the student
        List<StudentPromotionRecord> promotions = promotionRecordRepository
                .findAllByStudentIdOrderByPromotionDate(studentId);

        if (promotions.isEmpty()) {
            throw new RuntimeException("No promotion records found for this student.");
        }

        // Convert promotion records to DTO
        List<PromotionInfoDTO> promotionDTOs = promotions.stream()
                .map(this::mapToPromotionInfoDTO)
                .collect(Collectors.toList());

        // Extract the target standardId or streamId from the request
        Long targetStandardId = student.getStandard() != null ? student.getStandard().getSid() : null;
        Long targetStreamId = student.getStream() != null ? student.getStream().getId() : null;

        // Find the current promotion based on dynamic standardId or streamId
        Optional<PromotionInfoDTO> optionalPromotion = promotionDTOs.stream()
                .filter(dto -> {
                    if (targetStandardId != null) {
                        // Check for school promotions
                        return dto.getStandardId() != null && dto.getStandardId().equals(targetStandardId);
                    } else if (targetStreamId != null) {
                        // Check for college promotions
                        return dto.getStreamId() != null && dto.getStreamId().equals(targetStreamId);
                    }
                    return false;
                })
                .findFirst();

        // Determine the current promotion (based on the target)
        PromotionInfoDTO currentPromotion;
        if (optionalPromotion.isPresent()) {
            currentPromotion = optionalPromotion.get();
        } else {
            // Fallback to the most recent promotion if no match is found
            currentPromotion = promotionDTOs.get(promotionDTOs.size() - 1);
        }

        // Set the current promotion to true
        currentPromotion.setIsCurrent(true);

        // Set the remaining promotions as historical
        List<PromotionInfoDTO> promotionHistory = promotionDTOs.stream()
                .filter(dto -> !dto.equals(currentPromotion))
                .peek(dto -> dto.setIsCurrent(false)) // Mark as historical
                .collect(Collectors.toList());

        // Return the response with current and historical promotion records
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
        dto.setMediumName(record.getMediumName() != null ? record.getMedium().getMediumName() : null );

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

        dto.setStreamId(record.getStream() != null ? record.getStream().getId() : null);
        dto.setStreamName(record.getStreamName() !=null ? record.getStream().getStream() :null);

        dto.setGraduationTypeId(record.getGraduationType() != null ? record.getGraduationType().getId() : null);
        dto.setGraduationTypeName(record.getGraduationType() != null ? record.getGraduationType().getGraduationType() : null);
        dto.setDepartmentName(record.getDepartmentName());
        dto.setGroupName(record.getGroupName());

        return dto;
    }



}
