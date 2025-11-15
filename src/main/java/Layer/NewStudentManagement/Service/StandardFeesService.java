package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.FeesFilterDTO;
import Layer.NewStudentManagement.DTO.StandardFeesRequestDTO;
import Layer.NewStudentManagement.Entity.StudentStandardFees;
import jakarta.annotation.Nullable;

import java.util.List;

public interface StandardFeesService
{
    StandardFeesRequestDTO createStandardFees(String role, String email, StudentStandardFees standardFees);
    StandardFeesRequestDTO updateStandardFees(String role, String email,Long sfid, StudentStandardFees standardFees);
    StandardFeesRequestDTO getStandardFeesById(String role, String email, Long sfid);
    List<StandardFeesRequestDTO> getAllStandardFees(String role, String email, @Nullable String branchCodeFilter);
    void deleteStandardFee(String role, String email, Long sfid);
    List<StandardFeesRequestDTO> filterFees(String role, String email, FeesFilterDTO filterDTO);



}
