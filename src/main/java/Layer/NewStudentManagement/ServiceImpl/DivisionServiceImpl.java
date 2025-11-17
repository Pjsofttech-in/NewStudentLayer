package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.StudentDivisionDTO;
import Layer.NewStudentManagement.Entity.StudentDivision;
import Layer.NewStudentManagement.Repository.DivisionRepository;
import Layer.NewStudentManagement.Service.DivisionService;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DivisionServiceImpl implements DivisionService
{
    @Autowired
    private StaffService staffService;

    @Autowired
    private DivisionRepository divisionRepository;

    @Override
    public StudentDivision createDivision(String role, String email, StudentDivision division)
    {
        if(!staffService.hasPermission(role,email,"Post"))
        {
            throw new RuntimeException("You don't have permission to create division");
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        if (divisionRepository.existsByDivisionIgnoreCaseAndBranchCode(division.getDivision(), branchCode)) {
            throw new RuntimeException("Medium already exists for this branch");
        }
        division.setBranchCode(branchCode);
        division.setRole(role);
        division.setCreatedByEmail(email);
        return divisionRepository.save(division);
    }
    @Override
    public StudentDivisionDTO getDivisionById(Long id, String role, String email)
    {
        if(!staffService.hasPermission(role,email,"Get"))
        {
            throw new RuntimeException("You don't have permission to get division");
        }
        StudentDivision division = divisionRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Division not found"));
        return mapToDivisionDTO(division);
    }

    @Override
    public StudentDivision updateDivision(Long id,String role,String email,StudentDivision division)
    {
        if(!staffService.hasPermission(role,email,"Put"))
        {
            throw new RuntimeException("You don't have permission to update division");
        }
        StudentDivision existingDivision = divisionRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Division not found"));
        existingDivision.setDivision(division.getDivision());
        return divisionRepository.save(existingDivision);
    }

    @Override
    public void deleteDivisionById(Long id,String role,String email)
    {
        if(!staffService.hasPermission(role,email,"Delete"))
        {
            throw new RuntimeException("You don't have permission to delete division");
        }
        divisionRepository.deleteById(id);
    }

    @Override
    public List<StudentDivisionDTO> getAllDivision(String role, String email, @Nullable String branchCodeFilter)
    {
        if (!staffService.hasPermission(role, email, "Get")) {
            throw new RuntimeException("You don't have permission to get division");
        }

        if ("SUPERADMIN".equalsIgnoreCase(role)) {

            List<String> branchCodes = staffService.getBranchCodesByInstituteEmail(email);

            if (branchCodes == null || branchCodes.isEmpty()) {
                throw new RuntimeException("No branch codes found for this Superadmin");
            }

            if (branchCodeFilter != null && !branchCodeFilter.isBlank()) {

                if (!branchCodes.contains(branchCodeFilter)) {
                    throw new RuntimeException("Invalid branchCode for this Superadmin");
                }

                List<StudentDivision> filteredDivisions =
                        divisionRepository.findAllByBranchCode(branchCodeFilter);

                return filteredDivisions.stream()
                        .map(this::mapToDivisionDTO)
                        .collect(Collectors.toList());
            }

            List<StudentDivisionDTO> finalList = new ArrayList<>();

            for (String brCode : branchCodes) {

                List<StudentDivision> divisions =
                        divisionRepository.findAllByBranchCode(brCode);

                divisions.stream()
                        .map(this::mapToDivisionDTO)
                        .forEach(finalList::add);
            }

            return finalList;
        }
        String branchCode = staffService.fetchBranchCodeByRole(role, email);

        List<StudentDivision> divisions = divisionRepository.findAllByBranchCode(branchCode);

        return divisions.stream()
                .map(this::mapToDivisionDTO)
                .collect(Collectors.toList());
    }

    private StudentDivisionDTO mapToDivisionDTO(StudentDivision division) {
        return new StudentDivisionDTO(
                division.getDid(),
                division.getDivision(),
                division.getCreatedByEmail(),
                division.getRole(),
                division.getBranchCode()
        );
    }

}
