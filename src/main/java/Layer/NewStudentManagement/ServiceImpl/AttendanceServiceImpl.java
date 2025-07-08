package Layer.NewStudentManagement.ServiceImpl;
import Layer.NewStudentManagement.DTO.AttendanceCountDTO;
import Layer.NewStudentManagement.DTO.StudentAttendaceDTO;
import Layer.NewStudentManagement.DTO.StudentAttendanceFilterDTO;
import Layer.NewStudentManagement.Entity.StudentAttendance;
import Layer.NewStudentManagement.Entity.StudentClassRoom;
import Layer.NewStudentManagement.Entity.StudentEntity;
import Layer.NewStudentManagement.Pagination.StudentAttendanceSpecification;
import Layer.NewStudentManagement.Repository.AttendanceRepository;
import Layer.NewStudentManagement.Repository.ClassRoomRepository;
import Layer.NewStudentManagement.Repository.StudentRepository;
import Layer.NewStudentManagement.Service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ClassRoomRepository classRoomRepository;

    @Autowired
    private StudentRepository studentRepository;


    @Override
    public String markStudentsAttendance(List<Integer> rollNos, Long classroomId) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        StudentClassRoom classRoom = classRoomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found with ID: " + classroomId));

        String branchCode = classRoom.getBranchCode();
        String systemName = "student-sys";
        LocalTime classStartTime = classRoom.getStartTime();
        LocalTime earliestAllowed = classStartTime.minusMinutes(10);
        LocalTime latestAllowed = classStartTime.plusMinutes(20);
        if (now.isBefore(earliestAllowed) || now.isAfter(latestAllowed)) {
            throw new RuntimeException("Attendance can only be marked between " +
                    earliestAllowed + " and " + latestAllowed + " for class starting at " + classStartTime);
        }
        LocalTime loginTime = LocalTime.now();
        LocalTime startTime = classRoom.getStartTime();
        String status = loginTime.isAfter(startTime) ? "Late" : "On Time";

        List<StudentEntity> students = studentRepository.findByClassRoomIdAndRollNos(classroomId, rollNos);

        if (students.size() != rollNos.size()) {
            List<Integer> foundRollNos = students.stream()
                    .map(StudentEntity::getRollNo)
                    .toList();

            List<Integer> missingRollNos = rollNos.stream()
                    .filter(rn -> !foundRollNos.contains(rn))
                    .toList();

            throw new RuntimeException("Students not found with rollNos: " + missingRollNos + " in ClassRoom ID: " + classroomId);
        }
        Map<Integer, String> rollNoToNameMap = students.stream()
                .collect(Collectors.toMap(StudentEntity::getRollNo, StudentEntity::getFullName));


        List<StudentAttendance> toSave = new ArrayList<>();

        for (Integer rollNo : rollNos)
        {
            Optional<StudentAttendance> existing = attendanceRepository
                    .findAttendanceByRollNoAndClassroomIdAndDate(rollNo, classroomId, today);

            if (existing.isPresent()) {
                continue;
            }

            StudentAttendance attendance = new StudentAttendance();
            attendance.setRollNo(rollNo);
            attendance.setBranchCode(branchCode);
            attendance.setStudentName(rollNoToNameMap.get(rollNo));
            attendance.setClassroomId(classroomId);
            attendance.setSystemName(systemName);
            attendance.setDate(today);
            attendance.setLoginTime(now);
            attendance.setStatus(status);

            toSave.add(attendance);
        }

        if (!toSave.isEmpty()) {
            attendanceRepository.saveAll(toSave);
        }

        return toSave.size() + " student(s) marked present successfully.";
    }

    @Override
    public String logoutStudents(List<Integer> rollNos, Long classroomId) {
        LocalDate today = LocalDate.now();
        LocalTime logoutTime = LocalTime.now();

        List<String> successList = new ArrayList<>();
        List<String> alreadyLoggedOut = new ArrayList<>();
        List<String> notFound = new ArrayList<>();

        for (Integer rollNo : rollNos) {
            Optional<StudentAttendance> optionalAttendance =
                    attendanceRepository.findAttendanceByRollNoAndClassroomIdAndDate(rollNo, classroomId, today);

            if (optionalAttendance.isPresent()) {
                StudentAttendance attendance = optionalAttendance.get();

                if (attendance.getLogoutTime() != null) {
                    alreadyLoggedOut.add(String.valueOf(rollNo));
                    continue;
                }

                attendance.setLogoutTime(logoutTime);

                if (attendance.getLoginTime() != null) {
                    long minutes = java.time.Duration.between(attendance.getLoginTime(), logoutTime).toMinutes();
                    attendance.setWorkingMinutes(minutes);
                }

                attendanceRepository.save(attendance);
                successList.add(String.valueOf(rollNo));
            } else {
                notFound.add(String.valueOf(rollNo));
            }
        }

        return "✅ Logout Success: " + String.join(", ", successList) +
                "\nAlready Logged Out: " + String.join(", ", alreadyLoggedOut) +
                "\nNo Attendance Found: " + String.join(", ", notFound);
    }

    @Override
    public String markAttendanceFromFace(MultipartFile image, String branchCode) {
        try {
            String fastApiUrl = "https://pjsofttech.in:51443/auto-branch-scan";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            });
            body.add("branch_code", branchCode);
            body.add("system_name", "student-sys"); // Make sure this is handled in FastAPI if required

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.postForEntity(fastApiUrl, requestEntity, Map.class);

            Map<String, Object> responseBody = response.getBody();


            if (responseBody == null || !"success".equals(responseBody.get("status"))) {
                return "Face recognition failed";
            }

            List<Map<String, Object>> matches = (List<Map<String, Object>>) responseBody.get("matches");
            if (matches == null || matches.isEmpty()) {
                return "No face match found";
            }

            Map<String, Object> firstMatch = matches.get(0);
            String rollNo = (String) firstMatch.get("rollno");
            String classroomId = (String) firstMatch.get("classroomId");
            Long classroomIdLong = Long.parseLong(classroomId);
            LocalDate today = LocalDate.now();

            Optional<StudentAttendance> optional = attendanceRepository.findByRollNoAndDateAndClassroomId(rollNo, today, classroomIdLong);
            if (optional.isPresent()) {
                return "Attendance already marked for Roll No: " + rollNo;
            }

            StudentClassRoom classroom = classRoomRepository.findById(classroomIdLong)
                    .orElseThrow(() -> new RuntimeException("Classroom not found"));
            LocalTime classStartTime = classroom.getStartTime();
            LocalTime earliestAllowed = classStartTime.minusMinutes(10);
            LocalTime latestAllowed = classStartTime.plusMinutes(20);

            StudentEntity student = studentRepository.findByClassRoomIdAndRollNo(classroomIdLong, Integer.parseInt(rollNo))
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            LocalTime loginTime = LocalTime.now();
            LocalTime startTime = classroom.getStartTime();
            if (loginTime.isBefore(earliestAllowed) || loginTime.isAfter(latestAllowed)) {
                throw new RuntimeException("Attendance can only be marked between " +
                        earliestAllowed + " and " + latestAllowed + " for class starting at " + classStartTime);
            }
            String status = loginTime.isAfter(startTime) ? "Late" : "On Time";

            StudentAttendance attendance = new StudentAttendance();
            attendance.setRollNo(Integer.parseInt(rollNo));
            attendance.setStudentName(student.getFullName());
            attendance.setBranchCode(branchCode);
            attendance.setClassroomId(classroomIdLong);
            attendance.setLoginTime(loginTime);
            attendance.setDate(today);
            attendance.setStatus(status);

            attendanceRepository.save(attendance);
            return "Attendance marked for Roll No: " + rollNo +" Name:"+ attendance.getStudentName()+ " (" + status + ")";

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to mark attendance: " + e.getMessage();
        }
    }


    @Override
    public String logoutStudentFromFace(MultipartFile image, String branchCode) {
        try {
            String fastApiUrl = "https://pjsofttech.in:51443/auto-branch-scan"; // FastAPI endpoint

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            });
            body.add("branch_code", branchCode);
            body.add("system_name", "student-sys");

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.postForEntity(fastApiUrl, requestEntity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !"success".equalsIgnoreCase((String) responseBody.get("status"))) {
                return "Face recognition failed or no success status from server.";
            }

            List<Map<String, Object>> matches = (List<Map<String, Object>>) responseBody.get("matches");
            if (matches == null || matches.isEmpty()) {
                return "No face match found";
            }

            Map<String, Object> firstMatch = matches.get(0);
            String rollNo = String.valueOf(firstMatch.get("rollno"));
            String classroomIdStr = String.valueOf(firstMatch.get("classroomId"));
            Long classroomId = Long.parseLong(classroomIdStr);

            LocalDate today = LocalDate.now();
            Optional<StudentAttendance> optional = attendanceRepository.findByRollNoAndDateAndClassroomId(rollNo, today, classroomId);

            if (optional.isEmpty()) {
                return "No attendance record found for Roll No: " + rollNo;
            }

            StudentAttendance attendance = optional.get();

            if (attendance.getLogoutTime() != null) {
                return "Already logged out for Roll No: " + rollNo;
            }

            LocalTime logoutTime = LocalTime.now();
            attendance.setLogoutTime(logoutTime);

            if (attendance.getLoginTime() != null) {
                long workedMinutes = Duration.between(attendance.getLoginTime(), logoutTime).toMinutes();
                attendance.setWorkingMinutes(workedMinutes);
            }

            attendanceRepository.save(attendance);

            return "Logout successful for Roll No: " + rollNo;

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to logout student: " + e.getMessage();
        }
    }


//    @Override
//    public Page<StudentAttendance> getFilteredAttendance(
//            Long classroomId, StudentAttendanceFilterDTO filter, String timeFrame,
//            LocalDate customStartDate, LocalDate customEndDate, Pageable pageable) {
//        Specification<StudentAttendance> spec = StudentAttendanceSpecification.build(
//                filter, classroomId, timeFrame, customStartDate, customEndDate
//        );
//        return attendanceRepository.findAll(spec, pageable);
//    }


    @Override
    public Page<StudentAttendaceDTO> getFilteredAttendance(Long classroomId, StudentAttendanceFilterDTO filter, String timeFrame,
            LocalDate customStartDate, LocalDate customEndDate, Pageable pageable) {

        // 1. Determine date range
        LocalDate today = LocalDate.now();
        LocalDate startDate = today, endDate = today;

        switch (timeFrame.toLowerCase()) {
            case "7days" -> startDate = today.minusDays(6);
            case "30days" -> startDate = today.minusDays(29);
            case "365days" -> startDate = today.minusDays(364);
            case "custom" -> {
                if (customStartDate != null && customEndDate != null) {
                    startDate = customStartDate;
                    endDate = customEndDate;
                }
            }
        }

        // 2. Fetch and filter students
        List<StudentEntity> students = studentRepository.findAllByClassroomId(classroomId);

        if (filter != null) {
            if (filter.getRollNo() != null) {
                students = students.stream()
                        .filter(s -> s.getRollNo() == filter.getRollNo())
                        .toList();
            }
            if (filter.getStudentName() != null && !filter.getStudentName().isBlank()) {
                String name = filter.getStudentName().toLowerCase();
                students = students.stream()
                        .filter(s -> s.getFullName() != null && s.getFullName().toLowerCase().contains(name))
                        .toList();
            }
        }

        // 3. Fetch attendance records for the filtered date range
        Specification<StudentAttendance> spec = StudentAttendanceSpecification.build(
                filter, classroomId, timeFrame, customStartDate, customEndDate
        );
        List<StudentAttendance> attendanceList = attendanceRepository.findAll(spec);

        // 4. Map (rollNo + date) to attendance record
        Map<String, StudentAttendance> attendanceMap = attendanceList.stream()
                .collect(Collectors.toMap(
                        a -> a.getRollNo() + "_" + a.getDate(),
                        a -> a
                ));

        // 5. Build DTO list: present + absent
        List<StudentAttendaceDTO> combinedList = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            for (StudentEntity student : students) {
                String key = student.getRollNo() + "_" + date;
                StudentAttendance att = attendanceMap.get(key);

                StudentAttendaceDTO dto = (att != null)
                        ? new StudentAttendaceDTO(
                        att.getId(),
                        att.getRollNo(),
                        att.getBranchCode(),
                        att.getClassroomId(),
                        att.getStudentName(),
                        att.getSystemName(),
                        att.getDate(),
                        att.getLoginTime(),
                        att.getLogoutTime(),
                        att.getWorkingMinutes(),
                        att.getStatus()
                )
                        : new StudentAttendaceDTO(
                        null,
                        student.getRollNo(),
                        student.getBranchCode(),
                        classroomId,
                        student.getFullName(),
                        null,
                        date,
                        null,
                        null,
                        0L,
                        "Absent"
                );

                combinedList.add(dto);
            }
        }

        // 6. Apply `status` filter manually after combining data
        if (filter != null && filter.getStatus() != null && !filter.getStatus().equalsIgnoreCase("All")) {
            String status = filter.getStatus().toLowerCase();
            combinedList = combinedList.stream()
                    .filter(d -> d.getStatus() != null && d.getStatus().toLowerCase().equals(status))
                    .toList();
        }

        // 7. Return paginated result
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), combinedList.size());
        List<StudentAttendaceDTO> paged = combinedList.subList(start, end);

        return new PageImpl<>(paged, pageable, combinedList.size());
    }


    @Override
    public AttendanceCountDTO getAttendanceCountByTimeFrame(Long classroomId, String timeFrame,
                                                            LocalDate customStartDate, LocalDate customEndDate) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        switch (timeFrame.toLowerCase()) {
            case "today" -> {
                startDate = endDate;
            }
            case "7days" -> {
                startDate = endDate.minusDays(6); // Includes today
            }
            case "30days" -> {
                startDate = endDate.minusDays(29);
            }
            case "365days" -> {
                startDate = endDate.minusDays(364);
            }
            case "custom" -> {
                if (customStartDate == null || customEndDate == null) {
                    throw new IllegalArgumentException("Custom range requires both start and end dates.");
                }
                startDate = customStartDate;
                endDate = customEndDate;
            }
            default -> throw new IllegalArgumentException("Invalid timeFrame. Use 'today', '7days', '30days', '365days', or 'custom'.");
        }

        List<Integer> presentRollNos = attendanceRepository.findDistinctRollNosByDateRange(classroomId, startDate, endDate);
        Long totalStudents = studentRepository.countByClassroomId(classroomId);
        Long presentCount = (long) presentRollNos.size();
        Long absentCount = totalStudents - presentCount;

        return new AttendanceCountDTO(totalStudents, presentCount, absentCount);
    }



}