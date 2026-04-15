package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.StudentGraduationTypeDTO;
import Layer.NewStudentManagement.Entity.StudentGraduationType;

import java.util.List;

public interface GraduationTypeService
{

    StudentGraduationTypeDTO saveGraduationType(String role, String email, StudentGraduationTypeDTO request);
    StudentGraduationTypeDTO getGraduationTypeById(Long id, String role, String email);
    StudentGraduationTypeDTO updateGraduationType(Long id,String role,String email,StudentGraduationType graduationType);
    void deleteGraduationTypeById(Long id,String role,String email);
    List<StudentGraduationTypeDTO> getAllGraduationType(String role, String email);

    List<StudentGraduationTypeDTO> getGraduationTypesByStream(String role, String email, String streamName, String branchCode);
}
