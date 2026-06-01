package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentFeeComponentsMaster;
import Layer.NewStudentManagement.Repository.FeeComponentMasterRepository;
import Layer.NewStudentManagement.Service.FeeComponentMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class FeesComponentMasterServiceImpl implements FeeComponentMasterService {
    @Autowired
    private FeeComponentMasterRepository feeComponentMasterRepository;

    @Autowired
    private StaffService staffService;

    private void checkPermission(String role, String email, String action) {
        if (!staffService.hasPermission(role, email, action)) {
            throw new RuntimeException("You don't have permission to " + action.toLowerCase() + " Fees");
        }
    }

    @Override
    public StudentFeeComponentsMaster createFeeComponentMaster(String role, String email, StudentFeeComponentsMaster obj) {
        checkPermission(role, email, "POST");
        return feeComponentMasterRepository.save(obj);
    }

    @Override
    public StudentFeeComponentsMaster updateFeeComponentMaster(String role, String email, StudentFeeComponentsMaster obj) {
        checkPermission(role, email, "PUT");
        return feeComponentMasterRepository.save(obj);
    }

    @Override
    public void deleteFeeComponentMaster(String role, String email, Long id) {
        checkPermission(role, email, "DELETE");
        feeComponentMasterRepository.deleteById(id);
    }

    @Override
    public StudentFeeComponentsMaster getFeeComponentMaster(String role, String email, Long id, String name) {
        checkPermission(role, email, "GET");
        if (id != null) {
            return feeComponentMasterRepository.findById(id).orElseThrow(() -> new RuntimeException("Fees master with id " + id + " not found"));
        } else {
            List<StudentFeeComponentsMaster> list = searchByAnyColumnName("componentName", name);
            return CollectionUtils.isEmpty(list) ? null : list.getFirst();
        }
    }

    @Override
    public List<StudentFeeComponentsMaster> getAllFeeComponentMasters(String role, String email) {
        checkPermission(role, email, "GET");
        return feeComponentMasterRepository.findAll();
    }

    public List<StudentFeeComponentsMaster> searchByAnyColumnName(String columnPropertyName, Object searchKeyword) {
        // 🌟 Dynamically target the property name using the Criteria API under the hood
        Specification<StudentFeeComponentsMaster> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(columnPropertyName), searchKeyword);

        return feeComponentMasterRepository.findAll(spec);
    }
}
