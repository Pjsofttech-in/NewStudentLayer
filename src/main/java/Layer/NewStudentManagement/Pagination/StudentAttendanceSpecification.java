package Layer.NewStudentManagement.Pagination;

import Layer.NewStudentManagement.DTO.StudentAttendanceFilterDTO;
import Layer.NewStudentManagement.Entity.StudentAttendance;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentAttendanceSpecification {

    public static Specification<StudentAttendance> build(StudentAttendanceFilterDTO filter,
                                                         Long classroomId,
                                                         String timeFrame,
                                                         LocalDate customStartDate,
                                                         LocalDate customEndDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("classroomId"), classroomId));

            if (filter != null) {
                if (filter.getRollNo() != null) {
                    predicates.add(cb.equal(root.get("rollNo"), filter.getRollNo()));
                }
                if (filter.getStudentName() != null) {
                    predicates.add(cb.like(cb.lower(root.get("studentName")), "%" + filter.getStudentName().toLowerCase() + "%"));
                }
                if (filter.getStatus() != null) {
                    predicates.add(cb.equal(cb.lower(root.get("status")), filter.getStatus().toLowerCase()));
                }
            }

            LocalDate today = LocalDate.now();
            if (timeFrame != null) {
                switch (timeFrame.toLowerCase()) {
                    case "today":
                        predicates.add(cb.equal(root.get("date"), today));
                        break;
                    case "7days":
                        predicates.add(cb.between(root.get("date"), today.minusDays(6), today));
                        break;
                    case "30days":
                        predicates.add(cb.between(root.get("date"), today.minusDays(29), today));
                        break;
                    case "365days":
                        predicates.add(cb.between(root.get("date"), today.minusDays(364), today));
                        break;
                    case "custom":
                        if (customStartDate != null && customEndDate != null) {
                            predicates.add(cb.between(root.get("date"), customStartDate, customEndDate));
                        }
                        break;
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
