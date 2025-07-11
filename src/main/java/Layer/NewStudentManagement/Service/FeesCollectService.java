package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.FeesCollectDTO;
import Layer.NewStudentManagement.Entity.StudentFeesCollect;

import java.util.List;

public interface FeesCollectService
{
    FeesCollectDTO saveFeeCollection(StudentFeesCollect collect, String role, String email);
    FeesCollectDTO updateFeeCollectionStatus(Long id, String role, String email, String newStatus);
    List<FeesCollectDTO> getAllCollectDataByStudentFeesID(Long fid,String role,String email);

}
