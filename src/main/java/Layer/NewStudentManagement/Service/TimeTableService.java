package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.TimeTableRequestDTO;
import Layer.NewStudentManagement.DTO.TimeTableResponceDTO;
import Layer.NewStudentManagement.Entity.StudentTimetable;

import java.util.List;

public interface TimeTableService
{
    TimeTableResponceDTO createTimeTable(String role, String email, TimeTableRequestDTO dto);
    TimeTableResponceDTO getTimeTableById(String role, String email, Long id);
    List<TimeTableResponceDTO> getAllTimeTable(String role, String email);
//    TimeTableResponceDTO updateTimeTable(String role, String email, Long id, StudentTimetable timetable);
    void deleteTimeTable(String role, String email, Long id);
    List<TimeTableResponceDTO> getTimeTableByClassId(String role, String email, Long classId);
}
