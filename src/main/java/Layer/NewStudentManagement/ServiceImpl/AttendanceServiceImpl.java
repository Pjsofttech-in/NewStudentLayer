package Layer.NewStudentManagement.ServiceImpl;
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
            attendance.setStatus("Present");

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
    public String markAttendanceFromFace(MultipartFile image, String branchCode, String classroomId) {
        try {
            String fastApiUrl = "https://pjsofttech.in:51443/auto-login"; // adjust if needed

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
            body.add("classroomId", classroomId);
            body.add("system_name", "student-sys"); // default system

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.postForEntity(fastApiUrl, requestEntity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !"success".equals(responseBody.get("status"))) {
                return "Face recognition failed";
            }

            String rollNo = (String) responseBody.get("rollno");
            Long classroomIdLong = Long.parseLong(classroomId);
            LocalDate today = LocalDate.now();

            Optional<StudentAttendance> optional = attendanceRepository.findByRollNoAndDateAndClassroomId(rollNo, today, classroomIdLong);
            if (optional.isPresent()) {
                return "Attendance already marked for Roll No: " + rollNo;
            }

            // Fetch class start time
            StudentClassRoom classroom = classRoomRepository.findById(classroomIdLong)
                    .orElseThrow(() -> new RuntimeException("Classroom not found"));

            // ✅ Fetch student entity for name
            StudentEntity student = studentRepository.findByClassRoomIdAndRollNo(
                            classroomIdLong, Integer.parseInt(rollNo))
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            LocalTime loginTime = LocalTime.now();
            LocalTime startTime = classroom.getStartTime();
            String status = loginTime.isAfter(startTime) ? "Late" : "On Time";

            // Save attendance
            StudentAttendance attendance = new StudentAttendance();
            attendance.setRollNo(Integer.parseInt(rollNo));
            attendance.setStudentName(student.getFullName()); // ✅ set student name here
            attendance.setBranchCode(branchCode);
            attendance.setClassroomId(classroomIdLong);
            attendance.setLoginTime(loginTime);
            attendance.setDate(today);
            attendance.setStatus(status);

            attendanceRepository.save(attendance);
            return "Attendance marked for Roll No: " + rollNo + " (" + status + ")";

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to mark attendance: " + e.getMessage();
        }
    }


    @Override
    public String logoutStudentFromFace(MultipartFile image, String branchCode, String classroomId) {
        try {
            // Send image to Python FastAPI
            String fastApiUrl = "https://pjsofttech.in:51443/auto-logout"; // endpoint for logout

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
            body.add("classroomId", classroomId);
            body.add("system_name", "student-sys");

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.postForEntity(fastApiUrl, requestEntity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !"success".equals(responseBody.get("status"))) {
                return "Face recognition failed";
            }

            String rollNo = (String) responseBody.get("rollno");
            Long classroomIdLong = Long.parseLong(classroomId);
            LocalDate today = LocalDate.now();

            Optional<StudentAttendance> optional = attendanceRepository.findByRollNoAndDateAndClassroomId(rollNo, today, classroomIdLong);
            if (optional.isEmpty()) {
                return "No attendance record found for Roll No: " + rollNo;
            }

            StudentAttendance attendance = optional.get();
            if (attendance.getLogoutTime() != null) {
                return "Already logged out for Roll No: " + rollNo;
            }

            attendance.setLogoutTime(LocalTime.now());

            if (attendance.getLoginTime() != null) {
                long workedMinutes = Duration.between(attendance.getLoginTime(), attendance.getLogoutTime()).toMinutes();
                attendance.setWorkingMinutes(workedMinutes);
            }

            attendanceRepository.save(attendance);

            return "Logout successful for Roll No: " + rollNo;

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to logout student: " + e.getMessage();
        }
    }

    @Override
    public Page<StudentAttendance> getFilteredAttendance(
            Long classroomId, StudentAttendanceFilterDTO filter, String timeFrame,
            LocalDate customStartDate, LocalDate customEndDate, Pageable pageable) {
        Specification<StudentAttendance> spec = StudentAttendanceSpecification.build(
                filter, classroomId, timeFrame, customStartDate, customEndDate
        );
        return attendanceRepository.findAll(spec, pageable);
    }



}