package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StudentPeriodResponseDTO;
import Layer.NewStudentManagement.DTO.TimeTableRequestDTO;
import Layer.NewStudentManagement.DTO.TimeTableResponceDTO;
import Layer.NewStudentManagement.Entity.StudentClassRoom;
import Layer.NewStudentManagement.Entity.StudentPeriod;
import Layer.NewStudentManagement.Entity.StudentTimetable;
import Layer.NewStudentManagement.Exception.ResourceNotFoundException;
import Layer.NewStudentManagement.Repository.ClassRoomRepository;
import Layer.NewStudentManagement.Repository.PeriodRepository;
import Layer.NewStudentManagement.Repository.TimeTableRepository;
import Layer.NewStudentManagement.Service.TimeTableService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeTableServiceImpl implements TimeTableService
{

    @Autowired
    TimeTableRepository timeTableRepository;

    @Autowired
    ClassRoomRepository classRoomRepository;

    @Autowired
    PeriodRepository periodRepository;

    @Autowired
    StaffService staffService;


    @Override
    @Transactional
    public TimeTableResponceDTO createTimeTable(String role, String email, TimeTableRequestDTO dto) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to create Timetable");
        }

        StudentTimetable timetable = new StudentTimetable();
        timetable.setDayOfWeek(dto.getDayOfWeek());
        timetable.setRole(role);
        timetable.setCreatedByEmail(email);
        timetable.setBranchCode(staffService.fetchBranchCodeByRole(role, email));

        // Attach classroom
        StudentClassRoom classRoom = classRoomRepository.findById(dto.getClassRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found"));
        timetable.setClassRoom(classRoom);

        // Fetch periods
        List<StudentPeriod> attachedPeriods = periodRepository.findAllById(dto.getPeriodIds());
        for (StudentPeriod period : attachedPeriods) {
            period.setTimetable(timetable);
        }
        timetable.setPeriods(attachedPeriods);

        StudentTimetable timetable1 = timeTableRepository.save(timetable);
        return convertToDTO(timetable1);
    }


    @Override
    public TimeTableResponceDTO getTimeTableById(String role, String email, Long id) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to View Timetable");
        }
        StudentTimetable timetable = timeTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable not found with id " + id));
        return convertToDTO(timetable);
    }

    @Override
    public List<TimeTableResponceDTO> getAllTimeTable(String role, String email) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to View Timetable");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        return timeTableRepository.findAllByBranchCode(branchCode)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public TimeTableResponceDTO updateTimeTable(String role, String email, Long id, StudentTimetable timetable) {
        if (!staffService.hasPermission(role, email, "Put")) {
            throw new RuntimeException("You don't have permission to Update Timetable");
        }
        StudentTimetable existing = timeTableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Timetable not found with id " + id));


        if (timetable.getDayOfWeek() != null) {
            existing.setDayOfWeek(timetable.getDayOfWeek());
        }

        if (timetable.getClassRoom() != null) {
            existing.setClassRoom(timetable.getClassRoom());
        }

        if (timetable.getPeriods() != null && !timetable.getPeriods().isEmpty()) {
            existing.getPeriods().clear();
            timetable.getPeriods().forEach(period -> {
                period.setTimetable(existing);
                existing.getPeriods().add(period);
            });
        }
        existing.setCreatedByEmail(email);
        existing.setBranchCode(staffService.fetchBranchCodeByRole(role, email));
        existing.setRole(role);

        StudentTimetable timetable1 = timeTableRepository.save(existing);
        return convertToDTO(timetable1);
    }

    @Override
    public void deleteTimeTable(String role, String email, Long id) {
        if (!staffService.hasPermission(role, email, "Delete")) {
            throw new RuntimeException("You don't have permission to Delete Timetable");
        }
        StudentTimetable existing = timeTableRepository.getById(id);
        timeTableRepository.delete(existing);
    }

    @Override
    public List<TimeTableResponceDTO> getTimeTableByClassId(String role, String email, Long classId) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to View Timetable by Class");
        }
        return timeTableRepository.findByClassId(classId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    private TimeTableResponceDTO convertToDTO(StudentTimetable timetable)
    {
        TimeTableResponceDTO dto = new TimeTableResponceDTO();
        dto.setId(timetable.getId());
        dto.setDayOfWeek(timetable.getDayOfWeek());
        dto.setClassRoomId(timetable.getClassRoom() != null ? timetable.getClassRoom().getId() : null);

        if (timetable.getPeriods() != null) {
            dto.setPeriods(
                    timetable.getPeriods().stream().map(period -> {
                        StudentPeriodResponseDTO pDto = new StudentPeriodResponseDTO();
                        pDto.setId(period.getId());
                        pDto.setPeriodNo(period.getPeriodNo());
                        pDto.setStartTime(period.getStartTime() != null ? period.getStartTime().toString() : null);
                        pDto.setEndTime(period.getEndTime() != null ? period.getEndTime().toString() : null);

                        if (period.getSubject() != null) {
                            pDto.setSubjectId(period.getSubject().getId());
                            pDto.setSubjectName(period.getSubject().getSubject());
                        }

                        if (period.getTeacher() != null) {
                            pDto.setTeacherId(period.getTeacher().getId());
                            pDto.setTeacherName(period.getTeacher().getTeacherName());
                        }

                        return pDto;
                    }).toList()
            );
        } else {
            dto.setPeriods(null);
        }
        dto.setCreatedByEmail(timetable.getCreatedByEmail());
        dto.setBranchCode(timetable.getBranchCode());
        dto.setRole(timetable.getRole());

        return dto;
    }

}
