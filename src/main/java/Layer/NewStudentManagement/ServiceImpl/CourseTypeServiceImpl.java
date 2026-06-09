package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StudentCourseTypeDTO;
import Layer.NewStudentManagement.Entity.StudentCourseType;
import Layer.NewStudentManagement.Entity.StudentGraduationType;
import Layer.NewStudentManagement.Repository.CourseTypeRepository;
import Layer.NewStudentManagement.Repository.GraduationTypeRepository;
import Layer.NewStudentManagement.Service.CourseTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseTypeServiceImpl implements CourseTypeService {

    @Autowired
    private CourseTypeRepository courseTypeRepository;

    @Autowired
    private GraduationTypeRepository graduationTypeRepository;

    @Autowired
    private StaffService staffService;

    @Override
    public StudentCourseTypeDTO createCourseType(String role, String email, StudentCourseTypeDTO request) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to create courseType");
        }

        StudentGraduationType graduationType = graduationTypeRepository.findById(request.getGraduationTypeId())
                .orElseThrow(() -> new RuntimeException("GraduationType not found with ID: " + request.getGraduationTypeId()));

        StudentCourseType courseType = new StudentCourseType();
        courseType.setCourseType(request.getCourseType());
        courseType.setCreatedByEmail(email);
        courseType.setRole(role);
        courseType.setBranchCode(staffService.fetchBranchCodeByRole(role, email));
        courseType.setGraduationType(graduationType);

        StudentCourseType saved = courseTypeRepository.save(courseType);

        return mapToCourseTypeDTO(saved);
    }

    @Override
    public List<StudentCourseTypeDTO> getAllCourseTypes(String role, String email) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Get courseType");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        List<StudentCourseType> listOfCourseTypes = courseTypeRepository.findAllByBranchCode(branchCode);
        if (!listOfCourseTypes.isEmpty()) {
            return listOfCourseTypes.stream().map(this::mapToCourseTypeDTO).toList();
        }
        return new ArrayList<>();
    }

    @Override
    public StudentCourseTypeDTO getCourseTypesById(String role, String email, Long courseTypeId) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to Get courseType");
        }
        Optional<StudentCourseType> courseTypeOptional = courseTypeRepository.findById(courseTypeId);
        if (!courseTypeOptional.isEmpty()) {
            return mapToCourseTypeDTO(courseTypeOptional.get());
        }
        return new StudentCourseTypeDTO();
    }

    @Override
    public List<StudentCourseTypeDTO> getCourseTypesByGraduationType(String role, String email, Long graduationTypeId, String branchCode) {
        try {
            if ("STUDENT".equalsIgnoreCase(role) || "USER".equalsIgnoreCase(role) || "TEACHER".equalsIgnoreCase(role)) {

                // 🔥 FIX: auto-fetch branchCode
                String resolvedBranchCode = branchCode;
                if (resolvedBranchCode == null || resolvedBranchCode.isBlank()) {
                    resolvedBranchCode = staffService.fetchBranchCodeByRole(role, email);
                }

                if (resolvedBranchCode == null || resolvedBranchCode.isBlank()) {
                    throw new RuntimeException("BranchCode not found");
                }

                String finalBranchCode = resolvedBranchCode;
                return courseTypeRepository.findAllByGraduationType(graduationTypeId).stream()
                        .filter(d -> d.getBranchCode().equals(finalBranchCode))
                        .map(this::mapToCourseTypeDTO)
                        .collect(Collectors.toList());
            }

            // ✅ SUPERADMIN
            if ("SUPERADMIN".equalsIgnoreCase(role)) {

                if (!staffService.hasPermission(role, email, "GET")) {
                    throw new RuntimeException("You don't have permission to get CourseType");
                }

                List<String> instituteBranchCodes =
                        staffService.getBranchCodesByInstituteEmail(email);

                if (instituteBranchCodes == null || instituteBranchCodes.isEmpty()) {
                    throw new RuntimeException("No branches found for this institute email: " + email);
                }

                return courseTypeRepository.findAllByGraduationType(graduationTypeId).stream()
                        .filter(d -> instituteBranchCodes.contains(d.getBranchCode()))
                        .map(this::mapToCourseTypeDTO)
                        .collect(Collectors.toList());
            }

            // ✅ OTHER ROLES (ADMIN / STAFF)
            if (!staffService.hasPermission(role, email, "GET")) {
                throw new RuntimeException("You don't have permission to get CourseType");
            }

            String resolvedBranch =
                    staffService.fetchBranchCodeByRole(role, email);

            if (resolvedBranch == null || resolvedBranch.isBlank()) {
                throw new RuntimeException("BranchCode not found for role: " + role);
            }

            return courseTypeRepository.findAllByGraduationType(graduationTypeId).stream()
                    .filter(d -> d.getBranchCode().equals(resolvedBranch))
                    .map(this::mapToCourseTypeDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteCourseType(String role, String email, Long courseTypeId) {
        if (!staffService.hasPermission(role, email, "Delete")) {
            throw new RuntimeException("You don't have permission to Delete courseType");
        }

        courseTypeRepository.deleteById(courseTypeId);
    }

    private StudentCourseTypeDTO mapToCourseTypeDTO(StudentCourseType saved) {
        StudentCourseTypeDTO dto = new StudentCourseTypeDTO();
        dto.setId(saved.getId());
        dto.setCreatedByEmail(saved.getCreatedByEmail());
        dto.setRole(saved.getRole());
        dto.setCourseType(saved.getCourseType());
        dto.setBranchCode(saved.getBranchCode());
        dto.setGraduationTypeId(saved.getGraduationType().getId());
        return dto;
    }

}
