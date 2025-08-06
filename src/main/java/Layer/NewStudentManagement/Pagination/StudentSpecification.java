package Layer.NewStudentManagement.Pagination;

import Layer.NewStudentManagement.DTO.StudentFilterDTO;
import Layer.NewStudentManagement.Entity.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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

            // Joins to related tables
            Join<StudentEntity, StudentAdditionalInfo> additionalInfoJoin = root.join("additionalInfo", JoinType.LEFT);
            Join<StudentEntity, StudentReligion> religionJoin = root.join("religion", JoinType.LEFT);
            Join<StudentEntity, StudentSports> studentSportsJoin = root.join("sports", JoinType.LEFT);

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
                if (filter.getInstitutionType() != null) {
                    predicates.add(cb.equal(root.get("institutionType"), filter.getInstitutionType()));
                }

                Join<StudentEntity, StudentGraduationType> graduationJoin = root.join("graduationType", JoinType.LEFT);

                if (filter.getGraduationType() != null && !filter.getGraduationType().isEmpty()) {
                    predicates.add(cb.equal(cb.lower(graduationJoin.get("graduationType")), filter.getGraduationType().toLowerCase()));
                }

                Join<StudentEntity, StudentDegreeName> degreeJoin = root.join("degreeName", JoinType.LEFT);

                if (filter.getDegreeName() != null && !filter.getDegreeName().isEmpty()) {
                    predicates.add(cb.equal(cb.lower(degreeJoin.get("degreeName")), filter.getDegreeName().toLowerCase()));
                }
                Join<StudentEntity, StudentDepartment> departmentJoin = root.join("department", JoinType.LEFT);
                if (filter.getDepartmentName() != null && !filter.getDepartmentName().isEmpty()) {
                    predicates.add(cb.equal(cb.lower(departmentJoin.get("departmentName")), filter.getDepartmentName().toLowerCase()));
                }


                if (filter.getCastCategory() != null) {
                    predicates.add(cb.equal(religionJoin.get("castCategory"), filter.getCastCategory()));
                }
                if (filter.getMinority() != null) {
                    predicates.add(cb.equal(religionJoin.get("minority"), filter.getMinority()));
                }
                if (filter.getProjectDifferentiated() != null) {
                    predicates.add(cb.equal(additionalInfoJoin.get("projectDifferentiated"), filter.getProjectDifferentiated()));
                }
                if (filter.getEarthquake() != null) {
                    predicates.add(cb.equal(additionalInfoJoin.get("earthquake"), filter.getEarthquake()));
                }
                if (filter.getHandicap() != null) {
                    predicates.add(cb.equal(additionalInfoJoin.get("handicap"), filter.getHandicap()));
                }
                if (filter.getSportYesNo() != null) {
                    predicates.add(cb.equal(studentSportsJoin.get("sportYesNo"), filter.getSportYesNo()));
                }
                if (filter.getScholarship() != null) {
                    predicates.add(cb.equal(additionalInfoJoin.get("scholarship"), filter.getScholarship()));
                }

            }

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


    public static Specification<StudentEntity> withFilters(
            String institutionType, Long standardId, Long mediumId,
            Long graduationTypeId, Long streamId, String groupName,
            Long degreeNameId, Long departmentId, LocalDate startDate, LocalDate endDate) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (institutionType != null) {
                predicates.add(cb.equal(root.get("institutionType"), institutionType));
            }
            if (standardId != null) {
                predicates.add(cb.equal(root.get("standardId"), standardId));
            }
            if (mediumId != null) {
                predicates.add(cb.equal(root.get("mediumId"), mediumId));
            }
            if (graduationTypeId != null) {
                predicates.add(cb.equal(root.get("graduationTypeId"), graduationTypeId));
            }
            if (streamId != null) {
                predicates.add(cb.equal(root.get("streamId"), streamId));
            }
            if (groupName != null && !groupName.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("groupName")), "%" + groupName.toLowerCase() + "%"));
            }
            if (degreeNameId != null) {
                predicates.add(cb.equal(root.get("degreeNameId"), degreeNameId));
            }
            if (departmentId != null) {
                predicates.add(cb.equal(root.get("departmentId"), departmentId));
            }
            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("enrollmentDate"), startDate, endDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
