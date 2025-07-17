package Layer.NewStudentManagement.Pagination;

import Layer.NewStudentManagement.Entity.StudentFees;
import org.springframework.data.jpa.domain.Specification;

public class StudentFeesSpecification
{
    public static Specification<StudentFees> hasStudentName(String name) {
        return (root, query, cb) -> name == null || name.isBlank() ? null :
                cb.like(cb.lower(root.get("studentName")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<StudentFees> hasFeesStatus(String status) {
        return (root, query, cb) -> status == null || status.isBlank() ? null :
                cb.equal(cb.lower(root.get("feesStatus")), status.toLowerCase());
    }

    public static Specification<StudentFees> hasBranchCode(String branchCode) {
        return (root, query, cb) -> branchCode == null || branchCode.isBlank() ? null :
                cb.equal(root.get("branchCode"), branchCode);
    }

}
