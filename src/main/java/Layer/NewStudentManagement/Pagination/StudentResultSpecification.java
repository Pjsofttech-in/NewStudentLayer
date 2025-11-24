package Layer.NewStudentManagement.Pagination;

import Layer.NewStudentManagement.Entity.StudentResult;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

public class StudentResultSpecification
{
    public static Specification<StudentResult> filterBy(
            Long classRoomId,
            String studentName,
            String examName,
            String status
    ) {
        return (root, query, cb) -> {

            Predicate predicate = cb.conjunction();

            // Join student
            if (studentName != null && !studentName.isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("student").get("fullName")),
                                "%" + studentName.toLowerCase() + "%"));
            }

            // Join exam
            if (examName != null && !examName.isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("exam").get("examName")),
                                "%" + examName.toLowerCase() + "%"));
            }

            // Join details for status
            if (status != null && !status.isBlank()) {
                predicate = cb.and(predicate,
                        cb.equal(cb.lower(root.get("overAllStatus")),
                                status.toLowerCase()));
            }

            // Filter by classroom id
            if (classRoomId != null) {
                predicate = cb.and(predicate,
                        cb.equal(
                                root.get("exam").get("classRoom").get("id"),
                                classRoomId
                        ));
            }

            return predicate;
        };
    }
}
