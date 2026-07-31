package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.*;
import Layer.NewStudentManagement.Entity.*;
import Layer.NewStudentManagement.Repository.*;
import Layer.NewStudentManagement.Service.ClassRoomService;
import Layer.NewStudentManagement.Service.S3Service;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClassRoomServiceImpl implements ClassRoomService {
    @Autowired
    ClassRoomRepository classRoomRepository;

    @Autowired
    CourseTypeRepository courseTypeRepository;

    @Autowired
    MediumRepository mediumRepository;

    @Autowired
    FeesRepository feesRepository;

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

        StudentCourseType courseType = null;

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        String institutionType = dto.getInstitutionType();

        // ================== 🔥 DUPLICATE CHECK LOGIC ==================

        Long standardId = null;
        Long streamId = null;
        Long degreeId = null;
        Long courseTypeId = null;
        String departmentName = null;

        if ("School".equalsIgnoreCase(institutionType)) {

            if (dto.getStandardId() == null)
                throw new RuntimeException("Standard is required for School ClassRoom");

            standardId = dto.getStandardId();
        } else if ("College".equalsIgnoreCase(institutionType)) {

            if (dto.getGraduationTypeId() == null)
                throw new RuntimeException("GraduationType is required for College ClassRoom");

            StudentGraduationType graduationType = graduationTypeRepository.findById(dto.getGraduationTypeId())
                    .orElseThrow(() -> new RuntimeException("GraduationType not found"));

            // Jr College
            if ("Jr.College".equalsIgnoreCase(graduationType.getGraduationType())) {

                if (dto.getStandardId() == null)
                    throw new RuntimeException("Standard is required for Jr.College ClassRoom");

                if (dto.getStreamId() == null)
                    throw new RuntimeException("Stream is required for Jr.College ClassRoom");

                standardId = dto.getStandardId();
                streamId = dto.getStreamId();
            } else if ("Diploma".equalsIgnoreCase(graduationType.getGraduationType())) {

                if (dto.getStreamId() == null)
                    throw new RuntimeException("Stream is required for Diploma ClassRoom");

                if (dto.getDepartmentName() == null)
                    throw new RuntimeException("Department is required for Diploma ClassRoom");

                if (dto.getCourseTypeId() == null)
                    throw new RuntimeException("Course Type is required for Diploma ClassRoom");

                streamId = dto.getStreamId();
                courseTypeId = dto.getCourseTypeId();
                departmentName = dto.getDepartmentName();

                courseType = courseTypeRepository.findById(dto.getCourseTypeId())
                        .orElseThrow(() -> new RuntimeException("Course Type not found"));
            }
            // UG / PG
            else {

                if (dto.getStreamId() == null || dto.getDegreeNameId() == null) {
                    throw new RuntimeException("Stream and DegreeName are required for UG/PG ClassRoom");
                }

                if (dto.getDepartmentName() == null || dto.getDepartmentName().isEmpty()) {
                    throw new RuntimeException("DepartmentName is required for UG/PG ClassRoom");
                }

                streamId = dto.getStreamId();
                degreeId = dto.getDegreeNameId();
                departmentName = dto.getDepartmentName(); // ✅ included
            }
        }

        // UG / PG


        boolean exists = classRoomRepository.existsClassRoom(
                branchCode,
                dto.getYear(),
                dto.getMediumId(),
                dto.getDivisionId(),
                standardId,
                streamId,
                degreeId,
                courseTypeId,
                departmentName
        );

        if (exists) {
            throw new RuntimeException("ClassRoom already exists for this combination");
        }

        // ================== ORIGINAL CODE CONTINUES ==================

        StudentClassRoom classRoom = new StudentClassRoom();
        classRoom.setYear(dto.getYear());
        classRoom.setStartTime(dto.getStartTime());
        classRoom.setEndTime(dto.getEndTime());
        classRoom.setInstitutionType(dto.getInstitutionType());
        classRoom.setBranchCode(branchCode);
        classRoom.setCreatedByEmail(email);
        classRoom.setRole(role);
        classRoom.setMedium(medium);
        classRoom.setDivision(division);
        classRoom.setCreatedDate(LocalDate.now());

        if ("School".equalsIgnoreCase(institutionType)) {

            StudentStandard standard = standardRepository.findById(dto.getStandardId())
                    .orElseThrow(() -> new RuntimeException("Standard not found"));
            classRoom.setStandard(standard);
        } else if ("College".equalsIgnoreCase(institutionType)) {

            StudentGraduationType graduationType = graduationTypeRepository.findById(dto.getGraduationTypeId())
                    .orElseThrow(() -> new RuntimeException("GraduationType not found"));

            classRoom.setGraduationType(graduationType);

            if ("Jr.College".equalsIgnoreCase(graduationType.getGraduationType())) {

                StudentStandard standard = standardRepository.findById(dto.getStandardId())
                        .orElseThrow(() -> new RuntimeException("Standard not found"));
                classRoom.setStandard(standard);

                StudentStream stream = streamRepository.findById(dto.getStreamId())
                        .orElseThrow(() -> new RuntimeException("Stream not found"));
                classRoom.setStream(stream);

                classRoom.setGroupName(dto.getGroupName());
            } else if("Diploma".equalsIgnoreCase(graduationType.getGraduationType())) {
                StudentStream stream = streamRepository.findById(dto.getStreamId())
                        .orElseThrow(() -> new RuntimeException("Stream not found"));

                classRoom.setDepartmentName(dto.getDepartmentName());
                classRoom.setStream(stream);
                classRoom.setCourseType(courseType);
            } else {
                StudentStream stream = streamRepository.findById(dto.getStreamId())
                        .orElseThrow(() -> new RuntimeException("Stream not found"));

                StudentDegreeName degree = degreeNameRepository.findById(dto.getDegreeNameId())
                        .orElseThrow(() -> new RuntimeException("Degree not found"));

                classRoom.setDepartmentName(dto.getDepartmentName());
                classRoom.setStream(stream);
                classRoom.setDegreeName(degree);
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
    public StudentClassRoomResponseDTO updateClassRoom(Long id, String role,
                                                       String email, StudentClassRoomRequestDTO request) {

        // 🔐 Permission
        if (!staffService.hasPermission(role, email, "Put")) {
            throw new RuntimeException("No permission");
        }

        // 📌 Fetch existing
        StudentClassRoom existing = classRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClassRoom not found"));

        // ===============================
        // 🔥 DUPLICATE CHECK (SMART WAY)
        // ===============================
        boolean exists = classRoomRepository.existsClassRoom(
                existing.getBranchCode(),
                request.getYear(),
                request.getMediumId(),
                request.getDivisionId(),
                request.getStandardId(),
                request.getStreamId(),
                request.getDegreeNameId(),
                request.getCourseTypeId(),
                existing.getDepartmentName()
        );

        // ✅ Allow update if values are same (avoid self-match issue)
        boolean isSameRecord =
                Objects.equals(existing.getYear(), request.getYear()) &&
                        Objects.equals(existing.getGroupName(), request.getGroupName()) &&
                        Objects.equals(existing.getMedium() != null ? existing.getMedium().getMid() : null, request.getMediumId()) &&
                        Objects.equals(existing.getDivision() != null ? existing.getDivision().getDid() : null, request.getDivisionId()) &&
                        Objects.equals(existing.getStandard() != null ? existing.getStandard().getSid() : null, request.getStandardId()) &&
                        Objects.equals(existing.getStream() != null ? existing.getStream().getId() : null, request.getStreamId()) &&
                        Objects.equals(existing.getCourseType() != null ? existing.getCourseType().getId() : null, request.getCourseTypeId()) &&
                        Objects.equals(existing.getDegreeName() != null ? existing.getDegreeName().getId() : null, request.getDegreeNameId());

        if (exists && !isSameRecord) {
            throw new RuntimeException("ClassRoom already exists for this branch");
        }

        // ===============================
        // ✅ UPDATE BASIC FIELDS
        // ===============================
        existing.setYear(request.getYear());
        existing.setInstitutionType(request.getInstitutionType());
        existing.setStartTime(request.getStartTime());
        existing.setEndTime(request.getEndTime());
        existing.setGroupName(request.getGroupName());

        // ===============================
        // 🔗 RELATIONS
        // ===============================
        if (request.getMediumId() != null) {
            StudentMedium medium = mediumRepository.findById(request.getMediumId())
                    .orElseThrow(() -> new RuntimeException("Medium not found"));
            existing.setMedium(medium);
        }

        if (request.getDivisionId() != null) {
            StudentDivision division = divisionRepository.findById(request.getDivisionId())
                    .orElseThrow(() -> new RuntimeException("Division not found"));
            existing.setDivision(division);
        }

        if (request.getStandardId() != null) {
            StudentStandard standard = standardRepository.findById(request.getStandardId())
                    .orElseThrow(() -> new RuntimeException("Standard not found"));
            existing.setStandard(standard);
        }

        if (request.getGraduationTypeId() != null) {
            StudentGraduationType grad = graduationTypeRepository.findById(request.getGraduationTypeId())
                    .orElseThrow(() -> new RuntimeException("Graduation type not found"));
            existing.setGraduationType(grad);
        } else {
            existing.setGraduationType(null);
        }

        if (request.getCourseTypeId() != null) {
            StudentCourseType courseType = courseTypeRepository.findById(request.getCourseTypeId())
                    .orElseThrow(() -> new RuntimeException("Course type not found"));
            existing.setCourseType(courseType);
        } else {
            existing.setCourseType(null);
        }

        if (request.getStreamId() != null) {
            StudentStream stream = streamRepository.findById(request.getStreamId())
                    .orElseThrow(() -> new RuntimeException("Stream not found"));
            existing.setStream(stream);
        } else {
            existing.setStream(null);
        }

        if (request.getDegreeNameId() != null) {
            StudentDegreeName degree = degreeNameRepository.findById(request.getDegreeNameId())
                    .orElseThrow(() -> new RuntimeException("Degree not found"));
            existing.setDegreeName(degree);
        } else {
            existing.setDegreeName(null);
        }

        // ===============================
        // 🔥 TEACHER SUBJECT UPDATE
        // ===============================
        if (request.getTeacherSubjectMap() != null) {

            List<StudentClassRoomTeacherSubject> finalList = new ArrayList<>();

            for (Map.Entry<Long, List<Long>> entry : request.getTeacherSubjectMap().entrySet()) {

                StudentTeacher teacher = teacherRepository.findById(entry.getKey())
                        .orElseThrow(() -> new RuntimeException("Teacher not found"));

                List<StudentSubject> subjects = subjectRepository.findAllById(entry.getValue());

                StudentClassRoomTeacherSubject mapping = new StudentClassRoomTeacherSubject();
                mapping.setTeacher(teacher);
                mapping.setSubjects(subjects);
                mapping.setClassRoom(existing);

                finalList.add(mapping);
            }

            existing.getTeacherSubjectAssignments().clear();
            existing.getTeacherSubjectAssignments().addAll(finalList);
        }

        // 💾 SAVE
        StudentClassRoom saved = classRoomRepository.save(existing);

        return mapToResponseDTO(saved);
    }

    @Override
    public StudentClassRoomResponseDTO getClassRoomById(Long id, String role, String email) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Get ClassRoom");
        }

        StudentClassRoom classRooms = classRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClassRoom not found"));

        return mapToResponseDTO(classRooms);

    }

    @Override
    public void deleteClassRoomById(Long id, String role, String email) {
        if (!staffService.hasPermission(role, email, "Delete")) {
            throw new RuntimeException("You don't have permission to Delete ClassRoom");
        }
        boolean studentExists = studentRepository.existsByClassRoomId(id);

        if (studentExists) {
            throw new RuntimeException("Cannot delete classroom. Students are still assigned to this classroom.");
        }
        classRoomRepository.deleteById(id);
    }

    @Override
    public List<StudentClassRoomResponseDTO> getAllClassRoom(String role, String email) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Fetch ClassRoom");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
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

            if (document == null || document.getStudentPhoto() == null || document.getStudentPhoto().isBlank()) {
                throw new RuntimeException("Student photo is required for assignment. Missing for student ID: " + student.getId());
            }

            // Assign classroom and roll number
            student.setClassRoom(classroom);
            student.setRollNo(newRollNo);

            // ✅ NEW LOGIC: update rollNo in fees table if exists
            if (feesRepository.existsByStudentId(student.getId())) {
                feesRepository.updateRollNoByStudentId(student.getId(), newRollNo);
            }

            try {
                String newPhotoUrl = s3Service.copyStudentPhotoToAttendanceFaces(
                        document.getStudentPhoto(),
                        branchCode,
                        classroomId.toString(),
                        String.valueOf(newRollNo)
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

            if (mapping.getTeacher() != null) {
                dto.setTeacherId(mapping.getTeacher().getId());
                dto.setTeacherName(mapping.getTeacher().getTeacherName());
                dto.setTeacherEmail(mapping.getTeacher().getTeacherEmail());
            }

            dto.setSubjects(mapping.getSubjects() != null
                    ? mapping.getSubjects().stream()
                    .map(StudentSubject::getSubject)
                    .collect(Collectors.toList())
                    : Collections.emptyList());

            return dto;
        }).collect(Collectors.toList());

        StudentClassRoomResponseDTO dto = new StudentClassRoomResponseDTO();

        if (classroom != null) {
            dto.setId(classroom.getId());
            dto.setYear(classroom.getYear());

            dto.setMedium(classroom.getMedium() != null ? classroom.getMedium().getMediumName() : null);
            dto.setMediumId(classroom.getMedium() != null ? classroom.getMedium().getMid() : null);

            dto.setDivision(classroom.getDivision() != null ? classroom.getDivision().getDivision() : null);
            dto.setDivisionId(classroom.getDivision() != null ? classroom.getDivision().getDid() : null);

            dto.setStandard(classroom.getStandard() != null ? classroom.getStandard().getStandardName() : null);
            dto.setStandardId(classroom.getStandard() != null ? classroom.getStandard().getSid() : null);

            dto.setCourseTypeId(classroom.getCourseType() != null ? classroom.getCourseType().getId() : null);
            dto.setCourseType(classroom.getCourseType() != null ? classroom.getCourseType().getCourseType() : null);

            dto.setStartTime(classroom.getStartTime());
            dto.setEndTime(classroom.getEndTime());
            dto.setGroupName(classroom.getGroupName());

            dto.setGraduationType(classroom.getGraduationType() != null
                    ? classroom.getGraduationType().getGraduationType() : null);
            dto.setGraduationTypeId(classroom.getGraduationType() != null
                    ? classroom.getGraduationType().getId() : null);

            dto.setInstitutionType(classroom.getInstitutionType());

            dto.setStreamName(classroom.getStream() != null
                    ? classroom.getStream().getStream() : null);
            dto.setStreamId(classroom.getStream() != null
                    ? classroom.getStream().getId() : null);

            dto.setDegreeName(classroom.getDegreeName() != null
                    ? classroom.getDegreeName().getDegreeName() : null);
            dto.setDegreeNameId(classroom.getDegreeName() != null
                    ? classroom.getDegreeName().getId() : null);

            dto.setDepartmentName(classroom.getDepartmentName());

            dto.setBranchCode(classroom.getBranchCode());
            dto.setEmail(classroom.getCreatedByEmail());
            dto.setRole(classroom.getRole());
        }

        dto.setTeacherSubjectMappings(teacherSubjectDTOs);

        return dto;
    }

    @Override
    public void removeStudentsFromClassroom(String role, String email, Long classroomId, List<Long> studentIds) {
        if (!staffService.hasPermission(role, email, "Delete")) {
            throw new RuntimeException("You don't have permission to Remove Student From ClassRoom");
        }

        StudentClassRoom classroom = classRoomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));

        List<StudentEntity> students = studentRepository.findAllById(studentIds);

        for (StudentEntity student : students) {
            if (student.getClassRoom() == null || !student.getClassRoom().getId().equals(classroomId)) {
                throw new RuntimeException("Student ID " + student.getId() + " is not assigned to this classroom.");
            }

            StudentDocument document = documentRepository.findByStudent(student.getId());

            if (document != null && document.getStudentPhoto() != null) {
                try {
                    String attendancePhotoUrl = "https://" + bucketName + ".s3.amazonaws.com/"
                            + student.getBranchCode() + "/student-sys/attendance_faces/"
                            + classroomId + "/" + student.getRollNo()
                            + document.getStudentPhoto().substring(document.getStudentPhoto().lastIndexOf("."));

                    s3Service.deleteFileFromUrl(attendancePhotoUrl);

                } catch (Exception e) {
                    throw new RuntimeException("Failed to remove photo from classroom folder for student ID: " + student.getId(), e);
                }
            }

            student.setClassRoom(null);
            student.setRollNo(0);
        }

        studentRepository.saveAll(students);
    }


    @Override
    public List<StudentClassRoomResponseDTO> getClassroomDTOsByTeacherId(Long teacherId, String role, String email) {
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
                        newDto.setCourseType(classroom.getCourseType() != null ? classroom.getCourseType().getCourseType() : null);
                        newDto.setCertification(classroom.getCertification() != null ? classroom.getCertification().getCertification() : null);
                        newDto.setInstitutionType(classroom.getInstitutionType());
                        newDto.setStreamName(classroom.getStream() != null ? classroom.getStream().getStream() : null);
                        newDto.setDegreeName(classroom.getDegreeName() != null ? classroom.getDegreeName().getDegreeName() : null);
                        newDto.setDepartmentName(classroom.getDepartmentName());
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

    @Override
    public List<TeacherWithSubjectsDTO> getTeachersWithSubjectsByClassroom(String role, String email, Long classroomId) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Get Teacher and Subject by ClassRoom");
        }

        List<StudentClassRoomTeacherSubject> assignments = classRoomTeacherSubjectRepository.findByClassRoomIdWithSubjects(classroomId);

        return assignments.stream().map(a -> {
            TeacherWithSubjectsDTO dto = new TeacherWithSubjectsDTO();
            dto.setTeacherId(a.getTeacher().getId());
            dto.setTeacherName(a.getTeacher().getTeacherName());
            dto.setTeacherEmail(a.getTeacher().getTeacherEmail());
            dto.setSubjects(a.getSubjects().stream().map(s -> s.getSubject()).toList());
            return dto;
        }).toList();
    }

    @Override
    public List<SubjectByClassroomProjection> getAllSubjectsByClassroom(String role, String email, Long classroomId) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Get Subject by ClassRoom");
        }
        return classRoomTeacherSubjectRepository.getAllSubjectsAssignedToClassroom(classroomId);
    }


    @Override
    public List<StudentClassRoomResponseDTO> getClassRoomsByFilter(ClassRoomFilterRequest filter) {
        List<StudentClassRoom> classrooms = classRoomRepository.findByFilters(
                filter.getInstitutionType(),
                filter.getGraduationTypeId(),
                filter.getStreamId(),
                filter.getMediumId(),
                filter.getStandardId(),
                filter.getDegreeNameId(),
                filter.getDepartmentName(),
                filter.getGroupName(),
                filter.getYear()
        );
        return classrooms.stream()
                .map(this::mapToResponseDTO) // using your mapping method
                .collect(Collectors.toList());
    }

}
