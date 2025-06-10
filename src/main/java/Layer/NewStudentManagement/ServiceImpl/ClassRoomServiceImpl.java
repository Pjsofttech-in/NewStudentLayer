package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.ClassRoomRequestDTO;
import Layer.NewStudentManagement.DTO.StudentClassRoomResponseDTO;
import Layer.NewStudentManagement.DTO.TeacherWithSubjectsDTO;
import Layer.NewStudentManagement.Entity.*;
import Layer.NewStudentManagement.Repository.*;
import Layer.NewStudentManagement.Service.ClassRoomService;
import Layer.NewStudentManagement.Service.S3Service;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;

import java.util.List;
import java.util.Map;
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
    TeacherRepository teacherRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    ClassRoomTeacherSubjectRepository classRoomTeacherSubjectRepository;

    @Autowired
    StaffService staffService;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Override
    @Transactional
    public StudentClassRoomResponseDTO createClassRoom(String role, String email, ClassRoomRequestDTO dto) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to create ClassRoom");
        }

        StudentMedium medium = mediumRepository.findById(dto.getMediumId())
                .orElseThrow(() -> new RuntimeException("Medium not found"));

        StudentDivision division = divisionRepository.findById(dto.getDivisionId())
                .orElseThrow(() -> new RuntimeException("Division not found"));

        StudentStandard standard = standardRepository.findById(dto.getStandardId())
                .orElseThrow(() -> new RuntimeException("Standard not found"));

        StudentClassRoom classRoom = new StudentClassRoom();
        classRoom.setYear(dto.getYear());
        classRoom.setBranchCode(staffService.fetchBranchCodeByRole(role, email));
        classRoom.setCreatedByEmail(email);
        classRoom.setRole(role);
        classRoom.setMedium(medium);
        classRoom.setDivision(division);
        classRoom.setStandard(standard);

        StudentClassRoom savedClassRoom = classRoomRepository.save(classRoom);

        for (Map.Entry<Long, List<Long>> entry : dto.getTeacherSubjectMap().entrySet()) {
            Long teacherId = entry.getKey();
            List<Long> subjectIds = entry.getValue();

            StudentTeacher teacher = teacherRepository.findById(teacherId)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            List<StudentSubject> subjects = subjectRepository.findAllById(subjectIds);
            List<String> assignedSubjectNames = teacher.getSubjects()
                    .stream()
                    .map(StudentSubject::getSubject)
                    .collect(Collectors.toList());
            for (StudentSubject subject : subjects) {
                if (!assignedSubjectNames.contains(subject.getSubject())) {
                    throw new RuntimeException("Subject " + subject.getSubject() + " not assigned to teacher " + teacher.getTeacherName());
                }
            }

            StudentClassRoomTeacherSubject mapping = new StudentClassRoomTeacherSubject();
            mapping.setClassRoom(savedClassRoom);
            mapping.setTeacher(teacher);
            mapping.setSubjects(subjects);

            classRoomTeacherSubjectRepository.save(mapping);
        }

        return mapToResponseDTO(savedClassRoom);
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
    public String assignStudentsToClassroom(String role, String email, Long classroomId, List<Long> studentIds) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to Assign Student To ClassRoom");
        }

        String branchCode =staffService.fetchBranchCodeByRole(role, email);
        StudentClassRoom classroom = classRoomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));

        Integer maxRollNo = studentRepository.findMaxRollNoByClassRoomId(classroomId);
        int newRollNo = (maxRollNo != null) ? maxRollNo + 1 : 1;

        List<StudentEntity> students = studentRepository.findAllById(studentIds);

        for (StudentEntity student : students) {
            student.setClassRoom(classroom);
            student.setRollNo(newRollNo);

            StudentDocument document = documentRepository.findByStudent(student.getId());

            if (document != null && document.getStudentPhoto() != null) {
                try {
                    String originalUrl = document.getStudentPhoto();
                    String s3Prefix = "https://" + bucketName + ".s3.amazonaws.com/";
                    if (!originalUrl.startsWith(s3Prefix)) {
                        throw new RuntimeException("Invalid student photo URL for studentId: " + student.getId());
                    }

                    String sourceKey = originalUrl.substring(s3Prefix.length());

                    String extension = sourceKey.substring(sourceKey.lastIndexOf("."));

                    String destKey = branchCode + "/student_sys/attendance_faces/" + classroomId + "/" + newRollNo + extension;

                    CopyObjectRequest copyReq = CopyObjectRequest.builder()
                            .sourceBucket(bucketName)
                            .sourceKey(sourceKey)
                            .destinationBucket(bucketName)
                            .destinationKey(destKey)
                            .build();

                    s3Client.copyObject(copyReq);

                    // (Optional) Set new photo URL in document if needed
                    // document.setStudentPhoto(s3Prefix + destKey);
                    // studentDocumentRepository.save(document);

                } catch (Exception e) {
                    throw new RuntimeException("Failed to copy photo for student ID: " + student.getId(), e);
                }
            }

            newRollNo++;
        }

        studentRepository.saveAll(students);
        return "Students assigned to classroom successfully.";
    }

    private StudentClassRoomResponseDTO mapToResponseDTO(StudentClassRoom classroom) {
        List<StudentClassRoomTeacherSubject> mappings =
                classRoomTeacherSubjectRepository.findByClassRoomId(classroom.getId());

        List<TeacherWithSubjectsDTO> teacherSubjectDTOs = mappings.stream().map(mapping -> {
            TeacherWithSubjectsDTO dto = new TeacherWithSubjectsDTO();
            dto.setTeacherId(mapping.getTeacher().getId());
            dto.setTeacherName(mapping.getTeacher().getTeacherName());
            dto.setTeacherEmail(mapping.getTeacher().getTeacherEmail());
            dto.setSubjects(mapping.getSubjects().stream()
                    .map(StudentSubject::getSubject)
                    .collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());

        return new StudentClassRoomResponseDTO(
                classroom.getId(),
                classroom.getYear(),
                classroom.getMedium().getMedium(),
                classroom.getDivision().getDivision(),
                classroom.getStandard().getStandard(),
                classroom.getBranchCode(),
                classroom.getCreatedByEmail(),
                classroom.getRole(),
                teacherSubjectDTOs
        );
    }



}
