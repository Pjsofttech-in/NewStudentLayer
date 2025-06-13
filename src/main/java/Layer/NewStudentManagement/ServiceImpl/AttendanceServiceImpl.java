package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentAttendance;
import Layer.NewStudentManagement.Repository.AttendanceRepository;
import Layer.NewStudentManagement.Service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
@Service
public class AttendanceServiceImpl implements AttendanceService
{

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String markAttendance(MultipartFile image, String systemName, String branchCode, String classroomId) throws IOException {
        String url = "https://pjsofttech.in:51443/auto-login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("system_name", systemName != null ? systemName : "student-sys");
        body.add("branch_code", branchCode);
        if (classroomId != null) {
            body.add("classroomId", classroomId);
        }

        ByteArrayResource imageResource = new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        };
        body.add("image", imageResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {
                }
        );

        Map<String, Object> responseBody = response.getBody();

        if (responseBody != null && "success".equals(responseBody.get("status"))) {
            // ✅ Create and save attendance
            StudentAttendance attendance = new StudentAttendance();
            attendance.setRollNo((String) responseBody.get("rollno"));
            attendance.setBranch((String) responseBody.get("branch"));
            attendance.setClassroomId((String) responseBody.get("classroomId"));
            attendance.setSystemName(systemName != null ? systemName : "student-sys");
            attendance.setAttendanceDate(LocalDate.now());
            attendance.setLoginTime(LocalTime.now());
            attendance.setStatus("PRESENT");

            attendanceRepository.save(attendance);

            return "Attendance marked for roll no: " + responseBody.get("rollno");
        } else {
            return "Failed to mark attendance: " + (responseBody != null ? responseBody.get("message") : "No response");
        }
    }

}
