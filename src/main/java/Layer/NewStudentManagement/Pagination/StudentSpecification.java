package Layer.NewStudentManagement.Pagination;

import Layer.NewStudentManagement.DTO.StudentFilterDTO;
import Layer.NewStudentManagement.Entity.StudentEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentSpecification {

    public static Specification<StudentEntity> build(StudentFilterDTO filter,
                                                     String branchCode,
                                                     String timeFrame,
                                                     LocalDate customStart,
                                                     LocalDate customEnd) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("branchCode"), branchCode));

            if (filter != null) {
                if (filter.getFullName() != null) {
                    predicates.add(cb.like(cb.lower(root.get("fullName")), "%" + filter.getFullName().toLowerCase() + "%"));
                }
                if (filter.getGender() != null) {
                    predicates.add(cb.equal(root.get("gender"), filter.getGender()));
                }

                if (filter.getMotherTongue() != null) {
                    predicates.add(cb.equal(root.get("motherTongue"), filter.getMotherTongue()));
                }
                if (filter.getStandardName() != null) {
                    predicates.add(cb.equal(root.get("standardName"), filter.getStandardName()));
                }
                if (filter.getAcademicYear() != null) {
                    predicates.add(cb.equal(root.get("academicYear"), filter.getAcademicYear()));
                }
                if (filter.getMediumName() != null) {
                    predicates.add(cb.equal(root.get("mediumName"), filter.getMediumName()));
                }
                if (filter.getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), filter.getStatus()));
                }
                if (filter.getStreamName() != null) {
                    predicates.add(cb.equal(root.get("streamName"), filter.getStreamName()));
                }
                if (filter.getGroupName() != null) {
                    predicates.add(cb.equal(root.get("groupName"), filter.getGroupName()));
                }
                if (filter.getSemister() != null) {
                    predicates.add(cb.equal(root.get("semister"), filter.getSemister()));
                }
            }

            // Date range logic (based on enrollmentDate)
            if (timeFrame != null) {
                LocalDate today = LocalDate.now();
                switch (timeFrame.toLowerCase()) {
                    case "today":
                        predicates.add(cb.equal(root.get("enrollmentDate"), today));
                        break;
                    case "7days":
                        predicates.add(cb.between(root.get("enrollmentDate"), today.minusDays(6), today));
                        break;
                    case "30days":
                        predicates.add(cb.between(root.get("enrollmentDate"), today.minusDays(29), today));
                        break;
                    case "365days":
                        predicates.add(cb.between(root.get("enrollmentDate"), today.minusDays(364), today));
                        break;
                    case "custom":
                        if (customStart != null && customEnd != null) {
                            predicates.add(cb.between(root.get("enrollmentDate"), customStart, customEnd));
                        }
                        break;
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
