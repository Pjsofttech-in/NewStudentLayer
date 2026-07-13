package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.Entity.StudentWebsiteHeaders;

import java.util.List;
import java.util.Optional;

public interface WebsiteHeaderService {

    List<StudentWebsiteHeaders> getMenuTree();

    List<StudentWebsiteHeaders> getAllLinks();

    Optional<StudentWebsiteHeaders> getLinkById(Long id);

    StudentWebsiteHeaders createLink(StudentWebsiteHeaders newLink);

    Optional<StudentWebsiteHeaders> updateLink(Long id, StudentWebsiteHeaders updatedData);

    boolean deleteLink(Long id);
}
