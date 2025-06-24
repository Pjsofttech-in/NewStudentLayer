package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StudentDepartmentDTO;
import Layer.NewStudentManagement.Entity.StudentDegreeName;
import Layer.NewStudentManagement.Entity.StudentDepartment;
import Layer.NewStudentManagement.Repository.DegreeNameRepository;
import Layer.NewStudentManagement.Repository.DepartmentRepository;
import Layer.NewStudentManagement.Service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService
{

    @Autowired
    private StaffService staffService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    DegreeNameRepository degreeNameRepository;

    @Override
    public StudentDepartmentDTO saveDepartment(String role, String email, StudentDepartmentDTO request) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to create department");
        }

        StudentDegreeName degreeName = degreeNameRepository.findById(request.getDegreeNameId())
                .orElseThrow(() -> new RuntimeException("DegreeName not found with ID: " + request.getDegreeNameId()));

        StudentDepartment department = new StudentDepartment();
        department.setDepartmentName(request.getDepartmentName());
        department.setDegreeName(degreeName);
        department.setCreatedByEmail(email);
        department.setRole(role);
        department.setBranchCode(staffService.fetchBranchCodeByRole(role, email));

        StudentDepartment saved = departmentRepository.save(department);

        return mapToDepartmentDTO(saved);
    }

    @Override
    public StudentDepartmentDTO getDepartmentById(Long id, String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get department");
        }
        StudentDepartment department = departmentRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Division not found"));
        return mapToDepartmentDTO(department);
    }

    @Override
    public StudentDepartmentDTO updateDepartment(Long id,String role,String email,StudentDepartment department)
    {
        if(!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to update department");
        }
        StudentDepartment existingDepartment = departmentRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Division not found"));
        existingDepartment.setDepartmentName(department.getDepartmentName());
        StudentDepartment saved = departmentRepository.save(existingDepartment);
        return mapToDepartmentDTO(saved);


    }

    @Override
    public void deleteDepartmentById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Delete"))
        {
            throw new RuntimeException("You don't have permission to delete department");
        }
        departmentRepository.deleteById(id);

    }

    @Override
    public List<StudentDepartmentDTO> getAllDepartment(String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get department");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role,email);
        List<StudentDepartment> department = departmentRepository.findAllByBranchCode(branchCode);
        return department.stream()
                .map(this::mapToDepartmentDTO)
                .collect(Collectors.toList());

    }

    @Override
    public List<StudentDepartmentDTO> getDepartmentsByDegreeId(String role, String email, Long degreeId) {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to get departments");
        }

        List<StudentDepartment> departments = departmentRepository.findByDegreeNameId(degreeId);

        return departments.stream()
                .map(this::mapToDepartmentDTO)
                .collect(Collectors.toList());
    }


    private StudentDepartmentDTO mapToDepartmentDTO(StudentDepartment department) {
        StudentDepartmentDTO dto = new StudentDepartmentDTO();
        dto.setId(department.getId());
        dto.setDepartmentName(department.getDepartmentName());
        dto.setCreatedByEmail(department.getCreatedByEmail());
        dto.setRole(department.getRole());
        dto.setBranchCode(department.getBranchCode());

        if (department.getDegreeName() != null) {
            dto.setDegreeNameId(department.getDegreeName().getId());
            dto.setDegreeName(department.getDegreeName().getDegreeName());
        }

        return dto;
    }

}
