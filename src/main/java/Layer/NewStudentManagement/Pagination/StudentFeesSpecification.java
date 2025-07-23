package Layer.NewStudentManagement.Pagination;

import Layer.NewStudentManagement.DTO.FeesFilterDTO;
import Layer.NewStudentManagement.Entity.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class StudentFeesSpecification
{
    public static Specification<StudentFees> filterByDTOAndBranchCode(FeesFilterDTO dto, String branchCode) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always filter by branchCode
            predicates.add(criteriaBuilder.equal(root.get("branchCode"), branchCode));

            if (dto.getStandardName() != null && !dto.getStandardName().isEmpty()) {
                Join<StudentFees, StudentStandard> standardJoin = root.join("standard", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(standardJoin.get("standardName"), dto.getStandardName()));
            }

            if (dto.getMediumName() != null && !dto.getMediumName().isEmpty()) {
                Join<StudentFees, StudentMedium> mediumJoin = root.join("medium", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(mediumJoin.get("mediumName"), dto.getMediumName()));
            }

            if (dto.getStreamName() != null && !dto.getStreamName().isEmpty()) {
                Join<StudentFees, StudentStream> streamJoin = root.join("stream", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(streamJoin.get("streamName"), dto.getStreamName()));
            }

            if (dto.getGroupName() != null && !dto.getGroupName().isEmpty()) {
                Join<StudentFees, StudentGroup> groupJoin = root.join("group", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(groupJoin.get("groupName"), dto.getGroupName()));
            }

            if (dto.getDegreeName() != null && !dto.getDegreeName().isEmpty()) {
                Join<StudentFees, StudentDegreeName> degreeJoin = root.join("degree", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(degreeJoin.get("degreeName"), dto.getDegreeName()));
            }

            if (dto.getDepartmentName() != null && !dto.getDepartmentName().isEmpty()) {
                Join<StudentFees, StudentDepartment> deptJoin = root.join("department", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(deptJoin.get("departmentName"), dto.getDepartmentName()));
            }
            if (dto.getInstitutionType() != null && !dto.getInstitutionType().isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("institutionType")),
                        dto.getInstitutionType().toLowerCase()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
