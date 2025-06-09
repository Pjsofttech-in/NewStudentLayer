package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StudentClassRoomResponseDTO;
import Layer.NewStudentManagement.DTO.StudentDTO;
import Layer.NewStudentManagement.Entity.*;
import Layer.NewStudentManagement.Repository.*;
import Layer.NewStudentManagement.Service.ClassRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassRoomServiceImpl implements ClassRoomService
{
    @Autowired
    ClassRoomRepository classRoomRepository;

    @Autowired
    MediumRepository mediumRepository;

    @Autowired
    DivisionRepository divisionRepository;

    @Autowired
    StandardRepository standardRepository;

    @Autowired
    StaffService staffService;

    @Autowired
    StudentRepository studentRepository;



    @Override
    public StudentClassRoomResponseDTO createClassRoom(String role, String email, StudentClassRoom classRoom)
    {
        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create ClassRoom");
        }
        StudentMedium medium = mediumRepository.findById(classRoom.getMedium().getMid())
                .orElseThrow(() -> new RuntimeException("Medium not found"));

        StudentDivision division = divisionRepository.findById(classRoom.getDivision().getDid())
                .orElseThrow(() -> new RuntimeException("Division not found"));

        StudentStandard standard = standardRepository.findById(classRoom.getStandard().getSid())
                .orElseThrow(() -> new RuntimeException("Standard not found"));

        classRoom.setBranchCode(staffService.fetchBranchCodeByRole(role, email));
        classRoom.setCreatedByEmail(email);
        classRoom.setRole(role);
        classRoom.setMedium(medium);
        classRoom.setDivision(division);
        classRoom.setStandard(standard);
        StudentClassRoom saved = classRoomRepository.save(classRoom);

        return mapToResponseDTO(saved);

    }
    @Override
    public StudentClassRoomResponseDTO updateClassRoom(Long id, String role, String email, StudentClassRoom updateClassRoom) {
        if (!staffService.hasPermission(role, email, "Put")) {
            throw new RuntimeException("You don't have permission for Update ClassRoom");
        }

        StudentClassRoom existingClassRoom = classRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClassRoom not found"));

        if (updateClassRoom.getYear() != null) {
            existingClassRoom.setYear(updateClassRoom.getYear());
        }

        if (updateClassRoom.getClassName() != null) {
            existingClassRoom.setClassName(updateClassRoom.getClassName());
        }

        if (updateClassRoom.getMedium() != null && updateClassRoom.getMedium().getMid() != null) {
            StudentMedium medium = mediumRepository.findById(updateClassRoom.getMedium().getMid())
                    .orElseThrow(() -> new RuntimeException("Medium not found"));
            existingClassRoom.setMedium(medium);
        }

        if (updateClassRoom.getDivision() != null && updateClassRoom.getDivision().getDid() != null) {
            StudentDivision division = divisionRepository.findById(updateClassRoom.getDivision().getDid())
                    .orElseThrow(() -> new RuntimeException("Division not found"));
            existingClassRoom.setDivision(division);
        }

        if (updateClassRoom.getStandard() != null && updateClassRoom.getStandard().getSid() != null) {
            StudentStandard standard = standardRepository.findById(updateClassRoom.getStandard().getSid())
                    .orElseThrow(() -> new RuntimeException("Standard not found"));
            existingClassRoom.setStandard(standard);
        }
        StudentClassRoom updated = classRoomRepository.save(existingClassRoom);
        return mapToResponseDTO(updated);
    }

    @Override
    public StudentClassRoomResponseDTO getClassRoomById(Long id, String role, String email)
    {
        if (!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to Get ClassRoom");
        }

        StudentClassRoom classRooms = classRoomRepository.findById(id)
                .orElseThrow(()->new RuntimeException("ClassRoom not found"));

        return mapToResponseDTO(classRooms);

    }

    @Override
    public void deleteClassRoomById(Long id, String role, String email)
    {
        if (!staffService.hasPermission(role,email,"Delete"))
        {
            throw new RuntimeException("You don't have permission to Delete ClassRoom");
        }
        classRoomRepository.deleteById(id);
    }

    @Override
    public List<StudentClassRoomResponseDTO> getAllClassRoom(String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to Fetch ClassRoom");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role,email);
        List<StudentClassRoom> classRooms = classRoomRepository.getAllByBranchCode(branchCode);
        return classRooms.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

    }


    @Override
    public String assignStudentsToClassroom(String role,String email,Long classroomId, List<Long> studentIds) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to Assign Student To ClassRoom");
        }
        StudentClassRoom classroom = classRoomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));

        Integer maxRollNo = studentRepository.findMaxRollNoByClassRoomId(classroomId);
        int newRollNo = (maxRollNo != null) ? maxRollNo + 1 : 1;
        List<StudentEntity> students = studentRepository.findAllById(studentIds);

        for (StudentEntity student : students) {
            student.setClassRoom(classroom);
            student.setRollNo(newRollNo++);
        }

        studentRepository.saveAll(students);
        return "Students assigned to classroom successfully.";
        }

    private StudentClassRoomResponseDTO mapToResponseDTO(StudentClassRoom classroom) {
        return new StudentClassRoomResponseDTO(
                classroom.getId(),
                classroom.getYear(),
                classroom.getClassName(),
                classroom.getMedium().getMedium(),
                classroom.getDivision().getDivision(),
                classroom.getStandard().getStandard(),
                classroom.getBranchCode(),
                classroom.getCreatedByEmail(),
                classroom.getRole()

        );
    }


}
