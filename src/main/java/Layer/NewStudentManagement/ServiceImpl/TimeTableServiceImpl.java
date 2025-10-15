package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.ScheduledPeriodResponseDTO;
import Layer.NewStudentManagement.DTO.StudentPeriodResponseDTO;
import Layer.NewStudentManagement.DTO.TimeTableRequestDTO;
import Layer.NewStudentManagement.DTO.TimeTableResponceDTO;
import Layer.NewStudentManagement.Entity.*;
import Layer.NewStudentManagement.Exception.ResourceNotFoundException;
import Layer.NewStudentManagement.Repository.*;
import Layer.NewStudentManagement.Service.TimeTableService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    TeacherRepository teacherRepository;

    @Autowired
    ScheduledPeriodRepository scheduledPeriodRepository;

    @Autowired
    SubjectRepository subjectRepository;

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
        timetable.setClassroom(classRoom);

        dto.getScheduledPeriods().forEach(sp -> {
            StudentPeriod periodSlot = periodRepository.findById(sp.getPeriodSlotId())
                    .orElseThrow(() -> new ResourceNotFoundException("Period slot not found"));

            StudentTeacher teacher = teacherRepository.findById(sp.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

            StudentSubject subject = subjectRepository.findById(sp.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

            StudentScheduledPeriod scheduled = new StudentScheduledPeriod();
            scheduled.setTimetable(timetable);
            scheduled.setPeriodSlot(periodSlot);
            scheduled.setTeacher(teacher);
            scheduled.setSubject(subject);

            timetable.getScheduledPeriods().add(scheduled);
        });

        StudentTimetable savedTimetable = timeTableRepository.save(timetable);
        return convertToDTO(savedTimetable);
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
    public void deleteTimeTable(String role, String email, Long id) {
        if (!staffService.hasPermission(role, email, "Delete")) {
            throw new RuntimeException("You don't have permission to Delete Timetable");
        }
        StudentTimetable existing = timeTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable not found"));
        timeTableRepository.delete(existing);
    }

    @Override
    public List<TimeTableResponceDTO> getTimeTableByClassId(String role, String email, Long classId) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to View Timetable by Class");
        }

        LocalDate today = LocalDate.now();

        List<StudentTimetable> timetables = timeTableRepository.findByClassId(classId);

        for (StudentTimetable timetable : timetables) {

            List<StudentScheduledPeriod> basePeriods = timetable.getScheduledPeriods().stream()
                    .filter(p -> p.getPeriodDate() == null)
                    .toList();

            Map<String, StudentScheduledPeriod> overrideMap = timetable.getScheduledPeriods().stream()
                    .filter(p -> today.equals(p.getPeriodDate()))
                    .collect(Collectors.toMap(
                            p -> p.getPeriodSlot().getId() + "-" + p.getSubject().getId() + "-" + p.getTeacher().getId(),
                            Function.identity()
                    ));

            List<StudentScheduledPeriod> mergedPeriods = new ArrayList<>();
            for (StudentScheduledPeriod base : basePeriods) {
                String key = base.getPeriodSlot().getId() + "-" + base.getSubject().getId() + "-" + base.getTeacher().getId();

                StudentScheduledPeriod periodToShow = overrideMap.getOrDefault(key, base);
                mergedPeriods.add(periodToShow);
            }

            timetable.setScheduledPeriods(mergedPeriods);
        }

        return timetables.stream()
                .map(this::convertToDTO)
                .toList();
    }



    @Override
    public String markPeriodOff(String role, String email, Long timetableId,
                                Long subjectId, Long teacherId, Long slotId) {

        // Permission check
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to modify timetable");
        }

        LocalDate date =LocalDate.now();

        // 1️⃣ Check if this date already has an override
        var existing = scheduledPeriodRepository
                .findByDateEntry(timetableId, subjectId, teacherId, slotId, date);

        if (existing.isPresent()) {
            StudentScheduledPeriod period = existing.get();
            if ("Off".equalsIgnoreCase(period.getStatus())) {
                return "Period already marked as Off for " + date;
            } else {
                period.setStatus("Off");
                scheduledPeriodRepository.save(period);
                return "Period updated as Off for " + date;
            }
        }

        // 2️⃣ Get the base (weekly) period to clone from
        StudentScheduledPeriod base = scheduledPeriodRepository
                .findBasePeriod(timetableId, subjectId, teacherId, slotId)
                .orElseThrow(() ->
                        new RuntimeException("Base timetable period not found for the given parameters"));

        // 3️⃣ Create a new date-specific record
        StudentScheduledPeriod offPeriod = new StudentScheduledPeriod();
        offPeriod.setTimetable(base.getTimetable());
        offPeriod.setPeriodSlot(base.getPeriodSlot());
        offPeriod.setTeacher(base.getTeacher());
        offPeriod.setSubject(base.getSubject());
        offPeriod.setPeriodDate(date);
        offPeriod.setStatus("Off");

        scheduledPeriodRepository.save(offPeriod);

        return "New period marked as Off for " + date;
    }

    private TimeTableResponceDTO convertToDTO(StudentTimetable timetable) {
        if (timetable == null) {
            return null;
        }

        TimeTableResponceDTO dto = new TimeTableResponceDTO();
        dto.setId(timetable.getId());
        dto.setDayOfWeek(timetable.getDayOfWeek());
        dto.setClassRoomId(
                timetable.getClassroom() != null ? timetable.getClassroom().getId() : null
        );
        dto.setClassRoomName(
                timetable.getClassroom() != null ? timetable.getClassroom().getDivision().getDivision() : null
        );

        dto.setScheduledPeriods(
                timetable.getScheduledPeriods().stream().map(sp -> {
                    ScheduledPeriodResponseDTO spDto = new ScheduledPeriodResponseDTO();
                    spDto.setId(sp.getId());
                    spDto.setStatus(sp.getStatus());
                    spDto.setPeriodDate(sp.getPeriodDate());

                    if (sp.getPeriodSlot() != null) {
                        spDto.setPeriodSlotId(sp.getPeriodSlot().getId());
                        spDto.setPeriodNo(sp.getPeriodSlot().getPeriodNo());
                        spDto.setStartTime(
                                sp.getPeriodSlot().getStartTime() != null
                                        ? sp.getPeriodSlot().getStartTime().toString()
                                        : null
                        );
                        spDto.setEndTime(
                                sp.getPeriodSlot().getEndTime() != null
                                        ? sp.getPeriodSlot().getEndTime().toString()
                                        : null
                        );
                    }

                    if (sp.getTeacher() != null) {
                        spDto.setTeacherId(sp.getTeacher().getId());
                        spDto.setTeacherName(sp.getTeacher().getTeacherName());
                    }

                    if (sp.getSubject() != null) {
                        spDto.setSubjectId(sp.getSubject().getId());
                        spDto.setSubjectName(sp.getSubject().getSubject());
                    }

                    return spDto;
                }).toList()
        );

        dto.setCreatedByEmail(timetable.getCreatedByEmail());
        dto.setBranchCode(timetable.getBranchCode());
        dto.setRole(timetable.getRole());

        return dto;
    }


}
