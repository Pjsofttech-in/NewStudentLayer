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
                                                     LocalDate customEnd,
                                                     String staffEmail) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("branchCode"), branchCode));

            predicates.add(cb.notEqual(root.get("status"), "Rejected"));
            predicates.add(cb.notEqual(root.get("status"), "Pending"));

            if (staffEmail != null && !staffEmail.trim().isEmpty()) {
                predicates.add(cb.equal(
                        root.get("createdByEmail"),
                        staffEmail.trim()
                ));
            }

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
                if (filter.getStatus() != null && !"Rejected".equalsIgnoreCase(filter.getStatus())) {

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
            Long degreeNameId, Long departmentId, LocalDate startDate, LocalDate endDate, String academicYear) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (institutionType != null) {
                predicates.add(cb.equal(root.get("institutionType"), institutionType));
            }
            if (standardId != null) {
                predicates.add(cb.equal(root.get("standard").get("id"), standardId));
            }
            if (mediumId != null) {
                predicates.add(cb.equal(root.get("medium").get("id"), mediumId));
            }
            if (graduationTypeId != null) {
                predicates.add(cb.equal(root.get("graduationType").get("id"), graduationTypeId));
            }
            if (streamId != null) {
                predicates.add(cb.equal(root.get("stream").get("id"), streamId));
            }
            if (groupName != null && !groupName.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("groupName")), "%" + groupName.toLowerCase() + "%"));
            }
            if (degreeNameId != null) {
                predicates.add(cb.equal(root.get("degreeName").get("id"), degreeNameId));
            }
            if (departmentId != null) {
                predicates.add(cb.equal(root.get("department").get("id"), departmentId));
            }
            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("enrollmentDate"), startDate, endDate));
            }
            if (academicYear != null && !academicYear.trim().isEmpty()) { // <-- New filter for academicYear
                predicates.add(cb.equal(root.get("academicYear"), academicYear));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<StudentEntity> filter(
            String branchCode,
            StudentFilterDTO filter,
            String timeFrame,
            LocalDate customStart,
            LocalDate customEnd
    ) {
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
                if (filter.getInstitutionType() != null) {
                    predicates.add(cb.equal(root.get("institutionType"), filter.getInstitutionType()));
                }

                Join<Object, Object> graduationJoin = root.join("graduationType", JoinType.LEFT);
                if (filter.getGraduationType() != null) {
                    predicates.add(cb.equal(cb.lower(graduationJoin.get("graduationType")), filter.getGraduationType().toLowerCase()));
                }

                Join<Object, Object> degreeJoin = root.join("degreeName", JoinType.LEFT);
                if (filter.getDegreeName() != null) {
                    predicates.add(cb.equal(cb.lower(degreeJoin.get("degreeName")), filter.getDegreeName().toLowerCase()));
                }

                Join<Object, Object> departmentJoin = root.join("department", JoinType.LEFT);
                if (filter.getDepartmentName() != null) {
                    predicates.add(cb.equal(cb.lower(departmentJoin.get("departmentName")), filter.getDepartmentName().toLowerCase()));
                }

                // Extra filters
                if (filter.getCastCategory() != null) {
                    predicates.add(cb.equal(root.get("castCategory"), filter.getCastCategory()));
                }
                if (filter.getMinority() != null) {
                    predicates.add(cb.equal(root.get("minority"), filter.getMinority()));
                }
                if (filter.getProjectDifferentiated() != null) {
                    predicates.add(cb.equal(root.get("projectDifferentiated"), filter.getProjectDifferentiated()));
                }
                if (filter.getEarthquake() != null) {
                    predicates.add(cb.equal(root.get("earthquake"), filter.getEarthquake()));
                }
                if (filter.getHandicap() != null) {
                    predicates.add(cb.equal(root.get("handicap"), filter.getHandicap()));
                }
                if (filter.getSportYesNo() != null) {
                    predicates.add(cb.equal(root.get("sportYesNo"), filter.getSportYesNo()));
                }
                if (filter.getScholarship() != null) {
                    predicates.add(cb.equal(root.get("scholarship"), filter.getScholarship()));
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
}
