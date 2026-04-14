package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.*;
import Layer.NewStudentManagement.Entity.StudentClassRoom;
import Layer.NewStudentManagement.Entity.StudentClassRoomTeacherSubject;
import Layer.NewStudentManagement.Service.ClassRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "https://pjsofttech.in")
@RestController
public class ClassRoomController
{
    @Autowired
    ClassRoomService classRoomService;

    @PostMapping("/createClassRoom")
    public ResponseEntity<StudentClassRoomResponseDTO> createClassRoom(@RequestParam String role, @RequestParam String email, @RequestBody ClassRoomRequestDTO classRoom)
    {
        StudentClassRoomResponseDTO studentClassRoom = classRoomService.createClassRoom(role, email, classRoom);
        return ResponseEntity.ok(studentClassRoom);
    }

    @PutMapping("/updateClassRoom/{id}")
    public ResponseEntity<StudentClassRoomResponseDTO> updateClassRoom(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam String email,
            @RequestBody StudentClassRoomRequestDTO request
    ) {

        StudentClassRoomResponseDTO response = classRoomService.updateClassRoom(id, role, email, request);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/getClassRoomById/{id}")
    public ResponseEntity<StudentClassRoomResponseDTO> getClassRoomById(@PathVariable Long id, @RequestParam String email, @RequestParam String role)
    {
        StudentClassRoomResponseDTO studentClassRoom =classRoomService.getClassRoomById(id,role,email);
        return ResponseEntity.ok(studentClassRoom);
    }

    @GetMapping("/getAllClassRoom")
    public ResponseEntity<Iterable<StudentClassRoomResponseDTO>> getAllClassRoom(@RequestParam String role, @RequestParam String email)
    {
        Iterable<StudentClassRoomResponseDTO> studentClassRoom = classRoomService.getAllClassRoom(role, email);
        return ResponseEntity.ok(studentClassRoom);
    }

    @DeleteMapping("/deleteClassRoom/{id}")
    public ResponseEntity<String> deleteClassRoom(@PathVariable Long id,@RequestParam String role, @RequestParam String email)
    {
        classRoomService.deleteClassRoomById(id,role,email);
        return ResponseEntity.ok("ClassRoom deleted successfully.");
    }

    @PostMapping("/assignStudentToClassRoom")
    public ResponseEntity<Map<Long, String>> assignStudentsToClassroom(
            @RequestParam String role,
            @RequestParam String email,  @RequestBody AssignClassroomRequest request)
    {
        Map<Long, String> response = classRoomService.assignStudentsToClassroom(
                role,
                email,
                request.getClassroomId(),
                request.getStudentIds()
        );
        return ResponseEntity.ok(response);
    }


    @GetMapping("/getClassRoomByTeacherId/{teacherId}")
    public ResponseEntity<List<StudentClassRoomResponseDTO>> getClassroomDTOs(@PathVariable Long teacherId,@RequestParam String role, @RequestParam String email) {
        List<StudentClassRoomResponseDTO> dtos = classRoomService.getClassroomDTOsByTeacherId(teacherId,role,email);
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/getClassRoomByFilter")
    public ResponseEntity<List<StudentClassRoomResponseDTO>> getClassRoomsByFilter(
            @RequestBody ClassRoomFilterRequest filterRequest) {
        List<StudentClassRoomResponseDTO> classrooms = classRoomService.getClassRoomsByFilter(filterRequest);
        return ResponseEntity.ok(classrooms);
    }


    @DeleteMapping("/removeStudentFromClassRoom")
    public ResponseEntity<String> removeStudentsFromClassroom(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam Long classroomId,
            @RequestParam List<Long> studentIds) {
        classRoomService.removeStudentsFromClassroom(role, email, classroomId, studentIds);
        return ResponseEntity.ok("Students removed from classroom successfully.");
    }

    @GetMapping("/getTeacherAndSubjectByClassRoom")
    public List<TeacherWithSubjectsDTO> getTeachersWithSubjects(@RequestParam String role,
                                                                @RequestParam String email,
                                                                @RequestParam Long classroomId) {
        return classRoomService.getTeachersWithSubjectsByClassroom(role, email, classroomId);
    }
}
