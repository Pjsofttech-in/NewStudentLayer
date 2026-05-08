package Layer.NewStudentManagement.Pagination;

import Layer.NewStudentManagement.DTO.CreatedByResponseDTO;
import Layer.NewStudentManagement.DTO.FeesFilterDTO;
import Layer.NewStudentManagement.DTO.FeesRevenueFilterDTO;
import Layer.NewStudentManagement.Entity.*;
import Layer.NewStudentManagement.Util.HelperUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
                predicates.add(criteriaBuilder.equal(streamJoin.get("stream"), dto.getStreamName()));
            }

            if (dto.getGroupName() != null && !dto.getGroupName().isEmpty()) {
                Join<StudentFees, StudentGroup> groupJoin = root.join("group", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(groupJoin.get("groupName"), dto.getGroupName()));
            }

            if (dto.getDegreeName() != null && !dto.getDegreeName().isEmpty()) {
                Join<StudentFees, StudentDegreeName> degreeJoin = root.join("degree", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(degreeJoin.get("degreeName"), dto.getDegreeName()));
            }

//            if (dto.getDepartmentName() != null && !dto.getDepartmentName().isEmpty()) {
//                Join<StudentFees, StudentDepartment> deptJoin = root.join("department", JoinType.LEFT);
//                predicates.add(criteriaBuilder.equal(deptJoin.get("departmentName"), dto.getDepartmentName()));
//            }

            if (dto.getStudentName() != null && !dto.getStudentName().isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("studentName")),
                        dto.getStudentName().toLowerCase()
                ));
            }

            if (dto.getInstitutionType() != null && !dto.getInstitutionType().isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("institutionType")),
                        dto.getInstitutionType().toLowerCase()
                ));
            }

            if (dto.getFeesCollectionType() != null && !dto.getFeesCollectionType().isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("feesCollectionType")),
                        dto.getFeesCollectionType().toLowerCase()
                ));
            }

            if (dto.getFeesStatus() != null && !dto.getFeesStatus().isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("feesStatus")),
                        dto.getFeesStatus().toLowerCase()
                ));
            }

            if (StringUtils.isNotBlank(dto.getCreatedByEmail())) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("createdByEmail")),
                        dto.getCreatedByEmail()
                ));
            }

            if (StringUtils.isNotBlank(dto.getDueDate()) && HelperUtil.isStrictlyValidDate(dto.getDueDate())) {
                LocalDate parsedDate = HelperUtil.parseDateWithFormat(dto.getDueDate());

                Subquery<Long> subquery = null;
                subquery = query.subquery(Long.class);
                Root<StudentFeeSchedule> childRoot = subquery.from(StudentFeeSchedule.class);

                // 2. Define the link between Parent and Child
                // Assuming your ChildEntity has a field named 'mainObject' that links back to the parent
                Predicate parentLink = criteriaBuilder.equal(childRoot.get("studentFees"), root);

                // 3. Define your nested filters
                Predicate isUnpaid = criteriaBuilder.equal(childRoot.get("isPaid"), false);
                Predicate hasDate = criteriaBuilder.isNotNull(childRoot.get("dueDate"));
                Predicate isBefore = criteriaBuilder.lessThanOrEqualTo(childRoot.get("dueDate"), parsedDate);

                // 4. Configure the subquery to select IDs where conditions match
                subquery.select(childRoot.get("id"))
                        .where(parentLink, isUnpaid, hasDate, isBefore);
                predicates.add(criteriaBuilder.exists(subquery));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<StudentFees> withFilters(String branchCode, LocalDate startDate, LocalDate endDate, FeesRevenueFilterDTO filters) {
        return (root, query, cb) -> {
            query.multiselect(
                    cb.sum(root.get("feesAmount")),
                    cb.sum(root.get("paidAmount")),
                    cb.sum(root.get("pendingAmount"))
            );

            Predicate predicate = cb.equal(root.get("branchCode"), branchCode);

            if (startDate != null && endDate != null) {
                predicate = cb.and(predicate, cb.between(root.get("approvalDate"), startDate, endDate));
            }
            if (filters.getMonth() != null && !filters.getMonth().isBlank() && filters.getYear() != null) {
                try {
                    DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                            .parseCaseInsensitive()
                            .appendPattern("MMM")
                            .toFormatter(Locale.ENGLISH);

                    Month month = Month.from(formatter.parse(filters.getMonth()));
                    int monthValue = month.getValue();
                    int yearValue = filters.getYear().intValue();

                    LocalDate monthStart = LocalDate.of(yearValue, monthValue, 1);
                    LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

                    predicate = cb.and(predicate, cb.between(root.get("approvalDate"), monthStart, monthEnd));
                } catch (DateTimeParseException ex) {
                    throw new RuntimeException("Invalid month name: " + filters.getMonth() + ". Please use formats like Jan, Feb, Mar...");
                }
            }

            if (filters.getInstitutionType() != null && !filters.getInstitutionType().isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("institutionType"), filters.getInstitutionType()));
            }

            if (filters.getAcademicYear() != null && !filters.getAcademicYear().isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("student").get("academicYear"), filters.getAcademicYear()));
            }

            if (filters.getStandardName() != null && !filters.getStandardName().isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("standardName"), filters.getStandardName()));
            }

            if (filters.getMediumName() != null && !filters.getMediumName().isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("mediumName"), filters.getMediumName()));
            }

            if (filters.getStreamName() != null && !filters.getStreamName().isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("streamName"), filters.getStreamName()));
            }

            if (filters.getGraduationTypeName() != null && !filters.getGraduationTypeName().isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("degreeName"), filters.getGraduationTypeName()));
            }

            if (filters.getGroupName() != null && !filters.getGroupName().isBlank()) {
                predicate = cb.and(predicate, cb.equal(cb.lower(root.get("groupName")), filters.getGroupName().toLowerCase()));
            }

            if (filters.getDegreeName() != null && !filters.getDegreeName().isBlank()) {
                predicate = cb.and(predicate, cb.equal(cb.lower(root.get("degreeName")), filters.getDegreeName().toLowerCase()));
            }

            if (filters.getDepartmentName() != null && !filters.getDepartmentName().isBlank()) {
                predicate = cb.and(predicate, cb.equal(cb.lower(root.get("departmentName")), filters.getDepartmentName().toLowerCase()));
            }

            if (filters.getFeesStatus() != null && !filters.getFeesStatus().isBlank()) {
                predicate = cb.and(predicate, cb.equal(cb.lower(root.get("feesStatus")), filters.getFeesStatus().toLowerCase()));
            }

            if (filters.getFeesCollectionType() != null && !filters.getFeesCollectionType().isBlank()) {
                predicate = cb.and(predicate, cb.equal(cb.lower(root.get("feesCollectionType")), filters.getFeesCollectionType().toLowerCase()));
            }

            return predicate;
        };
    }
}
