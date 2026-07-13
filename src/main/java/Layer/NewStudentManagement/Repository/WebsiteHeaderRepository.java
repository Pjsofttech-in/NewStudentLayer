package Layer.NewStudentManagement.Repository;

import Layer.NewStudentManagement.Entity.StudentWebsiteHeaders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WebsiteHeaderRepository extends JpaRepository<StudentWebsiteHeaders, Long> {
    // Fetches only the root level items to build the menu tree
    List<StudentWebsiteHeaders> findByParentIsNullOrderBySortOrderAsc();
}
