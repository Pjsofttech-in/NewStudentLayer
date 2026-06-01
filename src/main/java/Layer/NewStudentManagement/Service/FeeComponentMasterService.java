package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.Entity.StudentFeeComponentsMaster;

import java.util.List;

public interface FeeComponentMasterService {
    public StudentFeeComponentsMaster createFeeComponentMaster(String role, String email, StudentFeeComponentsMaster obj);

    public StudentFeeComponentsMaster updateFeeComponentMaster(String role, String email, StudentFeeComponentsMaster obj);

    public void deleteFeeComponentMaster(String role, String email, Long id);

    public StudentFeeComponentsMaster getFeeComponentMaster(String role, String email, Long id, String name);

    public List<StudentFeeComponentsMaster> getAllFeeComponentMasters(String role, String email);
}
