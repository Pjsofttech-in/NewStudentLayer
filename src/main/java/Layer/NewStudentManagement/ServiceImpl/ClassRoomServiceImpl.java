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

import java.time.LocalDate;
import java.util.*;
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
    GraduationTypeRepository graduationTypeRepository;

    @Autowired
    StreamRepository streamRepository;

    @Autowired
    DegreeNameRepository degreeNameRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    private S3Service s3Service;


    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Override
    @Transactional
    public StudentClassRoomResponseDTO createClassRoom(String role, String email, ClassRoomRequestDTO dto) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to create ClassRoom");
        }

        if (dto.getMediumId() == null) {
            throw new RuntimeException("Medium is required for ClassRoom");
        }

        if (dto.getDivisionId() == null) {
            throw new RuntimeException("Division is required for ClassRoom");
        }

        if (dto.getYear() == null || dto.getYear().isEmpty()) {
            throw new RuntimeException("Academic year (year) is required for ClassRoom");
        }

        StudentMedium medium = mediumRepository.findById(dto.getMediumId())
                .orElseThrow(() -> new RuntimeException("Medium not found"));
        StudentDivision division = divisionRepository.findById(dto.getDivisionId())
                .orElseThrow(() -> new RuntimeException("Division not found"));

        StudentClassRoom classRoom = new StudentClassRoom();
        classRoom.setYear(dto.getYear());
        classRoom.setStartTime(dto.getStartTime());
        classRoom.setEndTime(dto.getEndTime());
        classRoom.setInstitutionType(dto.getInstitutionType());
        classRoom.setBranchCode(staffService.fetchBranchCodeByRole(role, email));
        classRoom.setCreatedByEmail(email);
        classRoom.setRole(role);
        classRoom.setMedium(medium);
        classRoom.setDivision(division);
        classRoom.setCreatedDate(LocalDate.now());
        String institutionType = dto.getInstitutionType();

        if ("School".equalsIgnoreCase(institutionType)) {
            if (dto.getStandardId() == null)
                throw new RuntimeException("Standard is required for School ClassRoom");

            StudentStandard standard = standardRepository.findById(dto.getStandardId())
                    .orElseThrow(() -> new RuntimeException("Standard not found"));
            classRoom.setStandard(standard);
        }

        else if ("College".equalsIgnoreCase(institutionType)) {
            if (dto.getGraduationTypeId() == null)
                throw new RuntimeException("GraduationType is required for College ClassRoom");

            StudentGraduationType graduationType = graduationTypeRepository.findById(dto.getGraduationTypeId())
                    .orElseThrow(() -> new RuntimeException("GraduationType not found"));
            classRoom.setGraduationType(graduationType);

            // Jr.College
            if ("Jr.College".equalsIgnoreCase(graduationType.getGraduationType())) {
                if (dto.getStandardId() == null)
                    throw new RuntimeException("Standard is required for Jr.College ClassRoom");

                StudentStandard standard = standardRepository.findById(dto.getStandardId())
                        .orElseThrow(() -> new RuntimeException("Standard not found"));
                classRoom.setStandard(standard);

                if (dto.getStreamId() == null)
                    throw new RuntimeException("Stream is required for Jr.College ClassRoom");

                StudentStream stream = streamRepository.findById(dto.getStreamId())
                        .orElseThrow(() -> new RuntimeException("Stream not found"));
                classRoom.setStream(stream);
                classRoom.setGroupName(dto.getGroupName());
            }

            // UG / PG
            else {
                if (dto.getStreamId() == null || dto.getDegreeNameId() == null || dto.getDepartmentId() == null) {
                    throw new RuntimeException("Stream, DegreeName and Department are required for UG/PG ClassRoom");
                }

                StudentStream stream = streamRepository.findById(dto.getStreamId())
                        .orElseThrow(() -> new RuntimeException("Stream not found"));
                StudentDegreeName degree = degreeNameRepository.findById(dto.getDegreeNameId())
                        .orElseThrow(() -> new RuntimeException("Degree not found"));
                StudentDepartment department = departmentRepository.findById(dto.getDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Department not found"));

                classRoom.setStream(stream);
                classRoom.setDegreeName(degree);
                classRoom.setDepartment(department);
            }
        }
        StudentClassRoom savedClassRoom = classRoomRepository.save(classRoom);

        if (dto.getTeacherSubjectMap() == null || dto.getTeacherSubjectMap().isEmpty()) {
            throw new RuntimeException("At least one teacher and subject mapping is required to create a ClassRoom");
        }

        for (Map.Entry<Long, List<Long>> entry : dto.getTeacherSubjectMap().entrySet()) {
            Long teacherId = entry.getKey();
            List<StudentSubject> subjects = subjectRepository.findAllById(entry.getValue());

            StudentTeacher teacher = teacherRepository.findById(teacherId)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

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
    public Map<Long, String> assignStudentsToClassroom(String role, String email, Long classroomId, List<Long> studentIds) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to Assign Student To ClassRoom");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        StudentClassRoom classroom = classRoomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));

        Integer maxRollNo = studentRepository.findMaxRollNoByClassRoomId(classroomId);
        int newRollNo = (maxRollNo != null) ? maxRollNo + 1 : 1;

        List<StudentEntity> students = studentRepository.findAllById(studentIds);
        Map<Long, String> studentPhotoUrls = new HashMap<>();

        for (StudentEntity student : students) {
            StudentDocument document = documentRepository.findByStudent(student.getId());
            //  Check if photo is present
            if (document == null || document.getStudentPhoto() == null || document.getStudentPhoto().isBlank()) {
                throw new RuntimeException("Student photo is required for assignment. Missing for student ID: " + student.getId());
            }
            //  Assign classroom and roll number
            student.setClassRoom(classroom);
            student.setRollNo(newRollNo);

            try {
                String newPhotoUrl = s3Service.copyStudentPhotoToAttendanceFaces(
                        document.getStudentPhoto(),
                        branchCode,
                        classroomId.toString(),
                        student.getRollNo() != null ? student.getRollNo().toString() : "N/A"
                );
                studentPhotoUrls.put(student.getId(), newPhotoUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to copy photo for student ID: " + student.getId(), e);
            }
            newRollNo++;
        }

        studentRepository.saveAll(students);
        return studentPhotoUrls;
    }


    private StudentClassRoomResponseDTO mapToResponseDTO(StudentClassRoom classroom) {
        List<StudentClassRoomTeacherSubject> mappings = Collections.emptyList();
        if (classroom != null && classroom.getId() != null) {
            mappings = classRoomTeacherSubjectRepository.findByClassRoomId(classroom.getId());
        }

        List<TeacherWithSubjectsDTO> teacherSubjectDTOs = mappings.stream().map(mapping -> {
            TeacherWithSubjectsDTO dto = new TeacherWithSubjectsDTO();
            dto.setTeacherId(mapping.getTeacher() != null ? mapping.getTeacher().getId() : null);
            dto.setTeacherName(mapping.getTeacher() != null ? mapping.getTeacher().getTeacherName() : null);
            dto.setTeacherEmail(mapping.getTeacher() != null ? mapping.getTeacher().getTeacherEmail() : null);
            dto.setSubjects(mapping.getSubjects() != null
                    ? mapping.getSubjects().stream()
                    .map(StudentSubject::getSubject)
                    .collect(Collectors.toList())
                    : Collections.emptyList());
            return dto;
        }).collect(Collectors.toList());

        return new StudentClassRoomResponseDTO(
                classroom != null ? classroom.getId() : null,
                classroom != null ? classroom.getYear() : null,
                (classroom != null && classroom.getMedium() != null) ? classroom.getMedium().getMediumName() : null,
                (classroom != null && classroom.getDivision() != null) ? classroom.getDivision().getDivision() : null,
                (classroom != null && classroom.getStandard() != null) ? classroom.getStandard().getStandardName() : null,
                classroom != null ? classroom.getStartTime() : null,
                classroom != null ? classroom.getEndTime() : null,
                classroom != null ? classroom.getGroupName() :null,
                (classroom != null && classroom.getGraduationType() != null) ? classroom.getGraduationType().getGraduationType() : null,
                classroom != null ? classroom.getInstitutionType() : null,
                (classroom != null && classroom.getStream() != null) ? classroom.getStream().getStream() : null,
                (classroom != null && classroom.getDepartment() != null) ? classroom.getDepartment().getDepartmentName() : null,
                (classroom != null && classroom.getDegreeName() != null) ? classroom.getDegreeName().getDegreeName() : null,
                classroom != null ? classroom.getBranchCode() : "",
                classroom != null ? classroom.getCreatedByEmail() : "",
                classroom != null ? classroom.getRole() : "",
                teacherSubjectDTOs
        );

    }

    @Override
    public List<StudentClassRoomResponseDTO> getClassroomDTOsByTeacherId(Long teacherId,String role, String email)
    {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Assign Student To ClassRoom");
        }
        List<StudentClassRoomTeacherSubject> assignments = classRoomTeacherSubjectRepository.findAssignmentsByTeacherId(teacherId);

        Map<Long, StudentClassRoomResponseDTO> classroomMap = new LinkedHashMap<>();

        for (StudentClassRoomTeacherSubject assignment : assignments) {
            StudentClassRoom classroom = assignment.getClassRoom();

            StudentClassRoomResponseDTO dto = classroomMap.computeIfAbsent(
                    classroom.getId(),
                    id -> {
                        StudentClassRoomResponseDTO newDto = new StudentClassRoomResponseDTO();
                        newDto.setId(classroom.getId());
                        newDto.setYear(classroom.getYear());
                        newDto.setMedium(classroom.getMedium() != null ? classroom.getMedium().getMediumName() : null);
                        newDto.setDivision(classroom.getDivision() != null ? classroom.getDivision().getDivision() : null);
                        newDto.setStandard(classroom.getStandard() != null ? classroom.getStandard().getStandardName() : null);
                        newDto.setStartTime(classroom.getStartTime());
                        newDto.setEndTime(classroom.getEndTime());
                        newDto.setGroupName(classroom.getGroupName());
                        newDto.setGraduationType(classroom.getGraduationType() != null ? classroom.getGraduationType().getGraduationType() : null);
                        newDto.setInstitutionType(classroom.getInstitutionType());
                        newDto.setStreamName(classroom.getStream() != null ? classroom.getStream().getStream() : null);
                        newDto.setDepartmentName(classroom.getDepartment() != null ? classroom.getDepartment().getDepartmentName() : null);
                        newDto.setDegreeName(classroom.getDegreeName() != null ? classroom.getDegreeName().getDegreeName() : null);
                        newDto.setBranchCode(classroom.getBranchCode());
                        newDto.setTeacherSubjectMappings(new ArrayList<>());
                        return newDto;
                    }
            );

            TeacherWithSubjectsDTO teacherDTO = new TeacherWithSubjectsDTO();
            StudentTeacher teacher = assignment.getTeacher();

            teacherDTO.setTeacherId(teacher.getId());
            teacherDTO.setTeacherName(teacher.getTeacherName());
            teacherDTO.setTeacherEmail(teacher.getTeacherEmail());

            List<String> subjectNames = assignment.getSubjects()
                    .stream()
                    .map(StudentSubject::getSubject)
                    .toList();

            teacherDTO.setSubjects(subjectNames);

            dto.getTeacherSubjectMappings().add(teacherDTO);
        }

        return new ArrayList<>(classroomMap.values());
    }


}
