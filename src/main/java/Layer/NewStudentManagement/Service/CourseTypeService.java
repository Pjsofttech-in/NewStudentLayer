package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.StudentCourseTypeDTO;

import java.util.List;

public interface CourseTypeService {
    StudentCourseTypeDTO createCourseType(String role, String email, StudentCourseTypeDTO dto);

    List<StudentCourseTypeDTO> getAllCourseTypes(String role, String email);

    StudentCourseTypeDTO getCourseTypesById(String role, String email, Long courseTypeId);

    List<StudentCourseTypeDTO> getCourseTypesByGraduationType(String role, String email, Long graduationTypeId, String branchCode);

    void deleteCourseType(String role, String email, Long courseTypeId);
}
