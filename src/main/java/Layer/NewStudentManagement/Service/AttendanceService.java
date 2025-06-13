package Layer.NewStudentManagement.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AttendanceService
{
    String markAttendance(MultipartFile image, String systemName, String branchCode, String classroomId) throws IOException;

}
