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
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
        LocalTime earliestAllowed = classStartTime.minusMinutes(60);
        LocalTime latestAllowed = classStartTime.plusMinutes(60);
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

        return "Logout Success: " + String.join(", ", successList) +
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
            LocalTime earliestAllowed = classStartTime.minusMinutes(60);
            LocalTime latestAllowed = classStartTime.plusMinutes(60);

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
            return "Attendance marked for Roll No: " + rollNo +"  Name:"+ attendance.getStudentName()+ " (" + status + ")";

        } catch (Exception e) {
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

            return "Logout successful for Roll No: " + rollNo+"  Name:"+ attendance.getStudentName();

        } catch (Exception e) {
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

        // 2. Fetch students of the classroom
        List<StudentEntity> students = studentRepository.findAllByClassroomId(classroomId);

        // 3. Apply filter on students (by rollNo or name)
        if (filter != null) {
            if (filter.getRollNo() != null) {
                students = students.stream()
                        .filter(s -> Objects.equals(s.getRollNo(), filter.getRollNo()))
                        .toList();
            }
            if (filter.getStudentName() != null && !filter.getStudentName().isBlank()) {
                String name = filter.getStudentName().toLowerCase();
                students = students.stream()
                        .filter(s -> s.getFullName() != null && s.getFullName().toLowerCase().contains(name))
                        .toList();
            }
        }

        Specification<StudentAttendance> spec = StudentAttendanceSpecification.build(
                filter, classroomId, timeFrame, customStartDate, customEndDate
        );
        List<StudentAttendance> attendanceList = attendanceRepository.findAll(spec);

        Map<String, StudentAttendance> attendanceMap = attendanceList.stream()
                .collect(Collectors.toMap(
                        a -> a.getRollNo() + "_" + a.getDate(),
                        a -> a
                ));

        List<StudentAttendaceDTO> combinedList = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            for (StudentEntity student : students) {
                String key = student.getRollNo() + "_" + date;
                StudentAttendance att = attendanceMap.get(key);

                StudentAttendaceDTO dto;
                if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    dto = new StudentAttendaceDTO(
                            null,
                            student.getRollNo(),
                            student.getBranchCode(),
                            student.getClassRoom().getId(),
                            student.getFullName(),
                            date,
                            null,
                            null,
                            0L,
                            "Sunday",
                            student.getId()
                    );
                } else if (att != null) {
                    dto = new StudentAttendaceDTO(
                            att.getId(),
                            att.getRollNo(),
                            att.getBranchCode(),
                            att.getClassroomId(),
                            att.getStudentName(),
                            att.getDate(),
                            att.getLoginTime(),
                            att.getLogoutTime(),
                            att.getWorkingMinutes(),
                            att.getStatus(),
                            student.getId()
                    );
                } else {
                    dto = new StudentAttendaceDTO(
                            null,
                            student.getRollNo(),
                            student.getBranchCode(),
                            student.getClassRoom().getId(),
                            student.getFullName(),
                            date,
                            null,
                            null,
                            0L,
                            "Absent",
                            student.getId()
                    );
                }

                combinedList.add(dto);
            }
        }

        if (filter != null && filter.getStatus() != null && !filter.getStatus().equalsIgnoreCase("All")) {
            String status = filter.getStatus().toLowerCase();
            combinedList = combinedList.stream()
                    .filter(d -> d.getStatus() != null && d.getStatus().toLowerCase().equals(status))
                    .toList();
        }

        // 8. Paginate manually
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), combinedList.size());
        List<StudentAttendaceDTO> pagedList = (start < end) ? combinedList.subList(start, end) : List.of();

        return new PageImpl<>(pagedList, pageable, combinedList.size());
    }


    @Override
    public AttendanceCountDTO getAttendanceCountByTimeFrame(
            Long classroomId, String timeFrame,
            LocalDate customStartDate, LocalDate customEndDate) {

        if (timeFrame == null) {
            throw new IllegalArgumentException("timeFrame cannot be null.");
        }

        LocalDate endDate = LocalDate.now();
        StudentClassRoom classroom = classRoomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));

        LocalDate classroomCreatedDate = classroom.getCreatedDate();
        LocalDate startDate;

        switch (timeFrame.toLowerCase()) {
            case "today" -> {
                startDate = endDate.isBefore(classroomCreatedDate) ? classroomCreatedDate : endDate;
            }
            case "7days" -> {
                LocalDate tempStart = endDate.minusDays(6);
                startDate = tempStart.isBefore(classroomCreatedDate) ? classroomCreatedDate : tempStart;
            }
            case "30days" -> {
                LocalDate tempStart = endDate.minusDays(29);
                startDate = tempStart.isBefore(classroomCreatedDate) ? classroomCreatedDate : tempStart;
            }
            case "365days" -> {
                LocalDate tempStart = endDate.minusDays(364);
                startDate = tempStart.isBefore(classroomCreatedDate) ? classroomCreatedDate : tempStart;
            }
            case "custom" -> {
                if (customStartDate == null || customEndDate == null) {
                    throw new IllegalArgumentException(
                            "For 'custom' timeFrame, both customStartDate and customEndDate must be provided."
                    );
                }
                startDate = customStartDate.isBefore(classroomCreatedDate) ? classroomCreatedDate : customStartDate;
                endDate = customEndDate.isAfter(LocalDate.now()) ? LocalDate.now() : customEndDate;
            }
            default -> throw new IllegalArgumentException(
                    "Invalid timeFrame. Use 'today', '7days', '30days', '365days', or 'custom'."
            );
        }

        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (numberOfDays < 0) numberOfDays = 0;

        List<StudentEntity> students = studentRepository.findByClassRoomId(classroomId);
        long totalStudents = students.size();

        long expectedEntries = totalStudents * numberOfDays;

        long presentCount = attendanceRepository.countByClassroomIdAndDateRange(classroomId, startDate, endDate);

        long absentCount = expectedEntries - presentCount;

        return new AttendanceCountDTO(expectedEntries, presentCount, absentCount);
    }


    @Override
    public Page<StudentAttendaceDTO> getAttendanceByStudentId(Long studentId, String filter,
                                                              LocalDate startDate, LocalDate endDate,
                                                              Pageable pageable) {

        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        LocalDate today = LocalDate.now();
        LocalDate fromDate;
        LocalDate toDate;

        String frame = (filter == null || filter.equalsIgnoreCase("all")) ? "all" : filter.toLowerCase();

        switch (frame.toLowerCase()) {
            case "today":
                fromDate = toDate = today;
                break;
            case "7days":
                fromDate = today.minusDays(6);
                toDate = today;
                break;
            case "30days":
                fromDate = today.minusDays(29);
                toDate = today;
                break;
            case "365days":
                fromDate = today.minusDays(364);
                toDate = today;
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start and end date must be provided for custom filter");
                }
                fromDate = startDate;
                toDate = endDate;
                break;
            case "all":
                fromDate = LocalDate.of(2000, 1, 1); // very early default
                toDate = today;
                break;
            default:
                throw new IllegalArgumentException("Invalid filter: " + filter);
        }

        LocalDate classStartDate = student.getClassRoom().getCreatedDate();

        if (classStartDate != null) {
            fromDate = fromDate.isBefore(classStartDate) ? classStartDate : fromDate;

            if (toDate.isBefore(classStartDate)) {
                toDate = classStartDate;
            }
        }

        List<StudentAttendance> attendances = attendanceRepository.findAttendanceByStudentAndDateRange(
                student.getRollNo(), student.getBranchCode(), student.getClassRoom().getId(), fromDate, toDate);

        Map<LocalDate, StudentAttendance> attendanceMap = attendances.stream()
                .collect(Collectors.toMap(StudentAttendance::getDate, att -> att));

        List<StudentAttendaceDTO> fullResponseList = new ArrayList<>();

        for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {
            StudentAttendaceDTO dto = new StudentAttendaceDTO();
            dto.setDate(date);
            dto.setStudentName(student.getFullName());
            dto.setRollNo(student.getRollNo());
            dto.setBranchCode(student.getBranchCode());
            dto.setClassroomId(student.getClassRoom().getId());
            dto.setStudentId(student.getId());

            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                dto.setStatus("Sunday");
            } else if (attendanceMap.containsKey(date)) {
                StudentAttendance att = attendanceMap.get(date);
                dto.setLoginTime(att.getLoginTime());
                dto.setLogoutTime(att.getLogoutTime());
                dto.setWorkingMinutes(att.getWorkingMinutes());
                dto.setStatus("Present");
            } else {
                dto.setStatus("Absent");
            }

            fullResponseList.add(dto);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), fullResponseList.size());
        List<StudentAttendaceDTO> paginatedList = fullResponseList.subList(start, end);

        return new PageImpl<>(paginatedList, pageable, fullResponseList.size());
    }


    @Override
    public Map<String, Long> getAttendanceCount(Long studentId, String filter,
                                                LocalDate startDate, LocalDate endDate) {
        // 1. Get student
        StudentEntity student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (student.getClassRoom() == null || student.getClassRoom().getId() == null) {
            throw new RuntimeException("Student is not assigned to any classroom.");
        }

        Long classroomId = student.getClassRoom().getId();
        int rollNo = student.getRollNo();

        LocalDate fromDate;
        LocalDate toDate = LocalDate.now();

        switch (filter.toLowerCase()) {
            case "today":
                fromDate = toDate;
                break;
            case "7days":
                fromDate = toDate.minusDays(6);
                break;
            case "30days":
                fromDate = LocalDate.of(toDate.getYear(), toDate.getMonth(), 1);
                break;
            case "365days":
                fromDate = LocalDate.of(toDate.getYear(), 1, 1);
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new RuntimeException("StartDate and EndDate must be provided for custom filter.");
                }
                fromDate = startDate;
                toDate = endDate;
                break;
            default:
                fromDate = student.getClassRoom().getCreatedDate();
                break;
        }
        Long presentCount = attendanceRepository.countPresentByStudentAndDateRange(
                rollNo, classroomId, fromDate, toDate);

        long totalDays = fromDate.datesUntil(toDate.plusDays(1))
                .filter(date -> date.getDayOfWeek() != java.time.DayOfWeek.SUNDAY)
                .count();

        long absentCount = totalDays - presentCount;

        Map<String, Long> result = new HashMap<>();
        result.put("presentCount", presentCount);
        result.put("absentCount", absentCount);

        return result;
    }




}