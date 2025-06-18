package Layer.NewStudentManagement.Util;

import org.springframework.beans.BeanUtils;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public class BeanCopyUtils {
    public static void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    private static String[] getNullPropertyNames(Object source) {
        final Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : BeanUtils.getPropertyDescriptors(source.getClass())) {
            try {
                Object value = pd.getReadMethod().invoke(source);
                if (value == null) {
                    emptyNames.add(pd.getName());
                }
            } catch (Exception ignored) {}
        }
        return emptyNames.toArray(new String[0]);
    }
}
