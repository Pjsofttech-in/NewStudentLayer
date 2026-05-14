package Layer.NewStudentManagement.Util;

import Layer.NewStudentManagement.Entity.StudentFees;
import Layer.NewStudentManagement.Entity.StudentFeesCollect;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Objects;

public class HelperUtil {
    public static String DATE_FORMAT = "uuuu-MM-dd";

    public static String getDateWithFormat(LocalDate date) {
        if (Objects.nonNull(date)) {
            return date.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        }
        return "";
    }

    public static LocalDate parseDateWithFormat(String date) {
        LocalDate parsedDate = null;
        if (StringUtils.isNotBlank(date)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
            parsedDate = LocalDate.parse(date, formatter);
        }
        return parsedDate;
    }

    public static boolean isStrictlyValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr,
                    DateTimeFormatter.ofPattern(DATE_FORMAT)
                            .withResolverStyle(ResolverStyle.STRICT)
            );
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static void addFeesCollectedByEmailFilterForStudentFees(Root<StudentFees> root, CriteriaQuery query, CriteriaBuilder cb, String email, List<Predicate> predicates){
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<StudentFeesCollect> childRoot = subquery.from(StudentFeesCollect.class);

//        This is applicable if we have @OneToMany relationship in StudentFees table
//        subquery.select(childRoot.get("id"));
//        Predicate parentLink = cb.equal(childRoot.get("studentFees"), root);
//        Predicate emailLink = cb.equal(childRoot.get("email"), email);
//
//        subquery.where(parentLink,emailLink);
//        predicates.add(cb.exists(subquery));

        subquery.select(childRoot.get("studentFees").get("fid"));
        subquery.where(cb.equal(childRoot.get("createdByEmail"), email));

        predicates.add(cb.in(root.get("fid")).value(subquery));
    }
}
