package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StudentResultDTO;
import Layer.NewStudentManagement.DTO.StudentResultDetailDTO;
import Layer.NewStudentManagement.Entity.*;
import Layer.NewStudentManagement.Pagination.StudentResultSpecification;
import Layer.NewStudentManagement.Repository.*;
import Layer.NewStudentManagement.Service.ResultService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResultServiceImpl implements ResultService
{
    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private StaffService staffService;

    @Autowired
    private ExamSubjectRepository examSubjectRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ExamRepository examRepository;


    @Autowired
    SubjectMarksRepository subjectMarksRepository;

    @Override
    @Transactional
    public StudentResultDTO createResult(StudentResult result, String role, String email) {
        if (!staffService.hasPermission(role, email, "POST")) {
            throw new RuntimeException("You don't have permission to create Result");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        result.setRole(role);
        result.setCreatedByEmail(email);
        result.setBranchCode(branchCode);

        boolean allPass = true;
        int totalObtained = 0;
        int totalMax = 0;

        if (result.getDetails() != null) {
            for (StudentResultDetail detail : result.getDetails()) {
                // ensure child is linked to parent
                detail.setStudentResult(result);
                detail.setRole(role);
                detail.setCreatedByEmail(email);
                detail.setBranchCode(branchCode);

                int obtained = detail.getObtainedMarks() != null ? detail.getObtainedMarks() : 0;
                totalObtained += obtained;
                Long subjectId = detail.getSubject().getId();
                StudentSubjectMarks subject = subjectMarksRepository.findById(subjectId)
                        .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));

                detail.setSubject(subject);

                totalMax += subject.getMaxMarks();

                Integer passingMarks = subject.getPassingMarks() != null ? subject.getPassingMarks() : 0;

                if (obtained >= passingMarks) {
                    detail.setStatus("Pass");
                } else {
                    detail.setStatus("Fail");
                    allPass = false;
                }
            }
        }

        result.setTotalObtained(totalObtained);
        result.setTotalMax(totalMax);
        result.setPercentage(totalMax > 0 ? (totalObtained * 100.0 / totalMax) : 0.0);

        if (allPass) {
            result.setOverAllStatus("Pass");
        } else {
            result.setOverAllStatus("Fail");
        }
        StudentResult result1 = resultRepository.save(result);
        return mapToResultDto(result1);
    }

    @Override
    @Transactional
    public StudentResultDTO updateResult(Long id, StudentResult updated, String role, String email) {
        if (!staffService.hasPermission(role, email, "PUT")) {
            throw new RuntimeException("You don't have permission to update Result");
        }

        StudentResult existing = resultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Result not found"));

        // Partial update logic
        if (updated.getStudent() != null) {
            existing.setStudent(updated.getStudent());
        }
        if (updated.getExam() != null) {
            existing.setExam(updated.getExam());
        }
        if (updated.getDetails() != null && !updated.getDetails().isEmpty()) {
            existing.getDetails().clear();
            for (StudentResultDetail detail : updated.getDetails()) {
                detail.setStudentResult(existing);
                detail.setRole(role);
                detail.setCreatedByEmail(email);
                detail.setBranchCode(existing.getBranchCode());
                existing.getDetails().add(detail);
            }
        }

        // Recalculate totals
        int totalObtained = 0;
        int totalMax = 0;
        for (StudentResultDetail detail : existing.getDetails()) {
            totalObtained += detail.getObtainedMarks();
            totalMax += detail.getSubject().getMaxMarks();
        }

        existing.setTotalObtained(totalObtained);
        existing.setTotalMax(totalMax);
        existing.setPercentage(totalMax > 0 ? (totalObtained * 100.0 / totalMax) : 0.0);

        StudentResult result1 = resultRepository.save(existing);
        return mapToResultDto(result1);
    }

    @Override
    public void deleteResult(Long id, String role, String email) {
        if (!staffService.hasPermission(role, email, "DELETE")) {
            throw new RuntimeException("You don't have permission to delete Result");
        }

        StudentResult existing = resultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Result not found"));

        resultRepository.delete(existing);
    }

    @Override
    public List<StudentResultDTO> getAllResults(String role, String email) {
        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to view Results");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        List<StudentResult> results = resultRepository.findResultByBranchCode(branchCode);
        return results.stream()
                .map(this::mapToResultDto)
                .collect(Collectors.toList());
    }

    @Override
    public StudentResultDTO getResultById(Long id, String role, String email) {
        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to view Result");
        }

        StudentResult result = resultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Result not found"));
        return mapToResultDto(result);
    }

    @Override
    public List<StudentResultDTO> getAcademicYearResults(String role, String email,Long studentId, String academicYear) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to update Result");
        }

        List<StudentResult> results;

        if (academicYear == null || academicYear.isEmpty()) {
            // fetch all results for the student
            results = resultRepository.findAllByStudentId(studentId);
        } else {
            // parse academic year
            String[] years = academicYear.split("-");
            int startYear = Integer.parseInt(years[0]);
            int endYear = Integer.parseInt(years[1]);

            LocalDate startDate = LocalDate.of(startYear, 6, 1);  // June 1
            LocalDate endDate = LocalDate.of(endYear, 5, 31);     // May 31

            results = resultRepository.findResultsForAcademicYear(studentId, startDate, endDate);
        }

        return results.stream()
                .map(this::mapToResultDto)
                .collect(Collectors.toList());
    }

    @Override
    public StudentResultDTO getLatestResultByStudentId(Long studentId, String role, String email) {
        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to view student results");
        }

        List<StudentResult> results = resultRepository.findLatestResultByStudentIdByExamDate(studentId);
        if (results.isEmpty()) {
            throw new RuntimeException("No results found for student: " + studentId);
        }
        return mapToResultDto(results.get(0));
    }


    @Override
    public List<StudentResultDTO> getResultsByClassRoom(String role, String email, Long examId, Long classRoomId) {

        if (!staffService.hasPermission(role, email, "GET")) {
            throw new RuntimeException("You don't have permission to view student results");
        }

        List<StudentEntity> students = studentRepository.findByClassRoomId(classRoomId);
        List<StudentResult> results = resultRepository.findByExamAndClassRoom(examId, classRoomId);

        Map<Long, StudentResult> resultMap = results.stream()
                .collect(Collectors.toMap(r -> r.getStudent().getId(), r -> r));

        return students.stream().map(student -> {

            // default dto
            StudentResultDTO dto = new StudentResultDTO();
            dto.setStudentId(student.getId());
            dto.setStudentName(student.getFullName());
            dto.setExamId(examId);
            dto.setExamName("");
            dto.setTotalObtained(0);
            dto.setTotalMax(0);
            dto.setPercentage(0.0);
            dto.setDetails(Collections.emptyList());
            dto.setOverAllStatus("Not Attempted");

            // If student has result
            if (resultMap.containsKey(student.getId())) {
                StudentResult result = resultMap.get(student.getId());

                StudentResultDTO mapped = mapToResultDto(result);

                // merge mapped fields
                dto.setId(mapped.getId());
                dto.setExamName(mapped.getExamName());
                dto.setOverAllStatus(mapped.getOverAllStatus());
                dto.setTotalObtained(mapped.getTotalObtained());
                dto.setTotalMax(mapped.getTotalMax());
                dto.setPercentage(mapped.getPercentage());
                dto.setDetails(mapped.getDetails());
            }

            return dto;
        }).toList();
    }


    @Override
    public Page<StudentResultDTO> getResults(Long classRoomId, String studentName, String examName,
                                             String status, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<StudentResult> results = resultRepository.findAll(
                StudentResultSpecification.filterBy(
                        classRoomId, studentName, examName, status
                ),
                pageable
        );

        return mapToResultDto(results); // now correct
    }



    private Page<StudentResultDTO> mapToResultDto(Page<StudentResult> results) {
        return results.map(this::mapToResultDto);
    }

    private StudentResultDTO mapToResultDto(StudentResult result) {
        StudentResultDTO dto = new StudentResultDTO();
        dto.setId(result.getId());
        dto.setStudentId(result.getStudent().getId());
        dto.setStudentName(result.getStudent().getFullName()); // assuming StudentEntity has getName()
        dto.setExamId(result.getExam().getId());
        dto.setRollNo(result.getStudent().getRollNo());
        dto.setExamType(result.getExam().getExamType());
//        dto.setDivision(result.getClass(),;
        dto.setExamName(result.getExam().getExamName());
        dto.setTotalObtained(result.getTotalObtained());
        dto.setOverAllStatus(result.getOverAllStatus());
        dto.setTotalMax(result.getTotalMax());
        dto.setPercentage(result.getPercentage());

        String overallStatus = result.getOverAllStatus();

        if (overallStatus == null || overallStatus.trim().isEmpty()) {
            boolean allPass = true;

            for (StudentResultDetail detail : result.getDetails()) {
                if (!"Pass".equalsIgnoreCase(detail.getStatus())) {
                    allPass = false;
                    break;
                }
            }

            overallStatus = allPass ? "Pass" : "Fail";
        }

        dto.setOverAllStatus(overallStatus);


        List<StudentResultDetailDTO> details = result.getDetails().stream()
                .map(detail -> {
                    StudentResultDetailDTO d = new StudentResultDetailDTO();
                    d.setSubjectId(detail.getSubject().getId());
                    d.setSubjectName(detail.getSubject().getSubjectName());
                    d.setMaxMarks(detail.getSubject().getMaxMarks());
                    d.setPassingMarks(detail.getSubject().getPassingMarks());
                    d.setObtainedMarks(detail.getObtainedMarks());
                    d.setStatus(detail.getStatus());
                    return d;
                })
                .collect(Collectors.toList());

        dto.setDetails(details);

        return dto;
    }

    @Override
    @Transactional
    public StudentResultDTO submitMark(Long studentId, Long examId, Long subjectId,
                                       Integer obtainedMarks, String role, String email) {

        if (!staffService.hasPermission(role, email, "POST")) {
            throw new RuntimeException("No permission");
        }

        StudentExamSubject examSubject = examSubjectRepository
                .findByExamIdAndSubjectId(examId, subjectId)
                .orElseThrow(() -> new RuntimeException("Exam or subject not found"));

        // Fetch or create result
        StudentResult result = resultRepository.findByStudentIdAndExamId(studentId, examId)
                .orElseGet(() -> {
                    StudentResult r = new StudentResult();
                    r.setStudent(studentRepository.findById(studentId).orElseThrow());
                    r.setExam(examRepository.findById(examId).orElseThrow());
                    r.setBranchCode(staffService.fetchBranchCodeByRole(role, email));
                    r.setCreatedByEmail(email);
                    r.setRole(role);
                    r.setDetails(new ArrayList<>());
                    return r;
                });

        if (result.getDetails() == null) {
            result.setDetails(new ArrayList<>());
        }

        // Check if detail exists or create new
        StudentResultDetail detail = result.getDetails().stream()
                .filter(d -> d.getSubject().getId().equals(subjectId))
                .findFirst()
                .orElseGet(() -> {
                    StudentResultDetail d = new StudentResultDetail();
                    d.setSubject(examSubject.getSubject());
                    d.setStudentResult(result);
                    d.setBranchCode(result.getBranchCode());
                    d.setCreatedByEmail(email);
                    d.setRole(role);
                    result.getDetails().add(d);
                    return d;
                });

        // Set obtained marks
        detail.setObtainedMarks(obtainedMarks);

        Integer passingMarks = detail.getSubject().getPassingMarks() != null
                ? detail.getSubject().getPassingMarks()
                : 0;

        if (obtainedMarks >= passingMarks) {
            detail.setStatus("Pass");
        } else {
            detail.setStatus("Fail");
        }

        int totalObtained = result.getDetails().stream()
                .mapToInt(d -> d.getObtainedMarks() != null ? d.getObtainedMarks() : 0)
                .sum();

        int totalMax = result.getDetails().stream()
                .mapToInt(d -> d.getSubject().getMaxMarks())
                .sum();

        double percentage = totalMax > 0 ? (totalObtained * 100.0 / totalMax) : 0.0;

        result.setTotalObtained(totalObtained);
        result.setTotalMax(totalMax);
        result.setPercentage(percentage);

        boolean allPass = result.getDetails().stream()
                .allMatch(d -> "Pass".equalsIgnoreCase(d.getStatus()));

        result.setOverAllStatus(allPass ? "Pass" : "Fail");

        resultRepository.save(result);

        return mapToResultDto(result);
    }



}
