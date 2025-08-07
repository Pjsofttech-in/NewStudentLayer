package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.TCDTO;
import Layer.NewStudentManagement.Entity.StudentTcData;

import java.util.List;

public interface TCDataService
{
    TCDTO generateTc(Long studentId, String role, String email);
    List<TCDTO> getAllTCByBranchCode(String role, String email);
    List<TCDTO> getAllTCByStudentId(Long studentId, String role,String email);

}
