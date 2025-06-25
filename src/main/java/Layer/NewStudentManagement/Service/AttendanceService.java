package Layer.NewStudentManagement.Service;


import Layer.NewStudentManagement.DTO.AttendanceCountDTO;
import Layer.NewStudentManagement.DTO.StudentAttendaceDTO;
import Layer.NewStudentManagement.DTO.StudentAttendanceFilterDTO;
import Layer.NewStudentManagement.Entity.StudentAttendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService
{

    String markStudentsAttendance(List<Integer> rollNos, Long classroomId);

    String logoutStudents(List<Integer> rollNos, Long classroomId);
    String markAttendanceFromFace(MultipartFile imagePath, String branchCode);

    String logoutStudentFromFace(MultipartFile image, String branchCode, String classroomId);

    Page<StudentAttendaceDTO> getFilteredAttendance(Long classroomId, StudentAttendanceFilterDTO filter, String timeFrame,
                                                    LocalDate customStartDate, LocalDate customEndDate, Pageable pageable) ;

    AttendanceCountDTO getAttendanceCountByTimeFrame(Long classroomId, String timeFrame,LocalDate customStartDate, LocalDate customEndDate);

}
