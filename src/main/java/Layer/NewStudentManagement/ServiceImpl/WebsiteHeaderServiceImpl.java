package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.Entity.StudentWebsiteHeaders;
import Layer.NewStudentManagement.Repository.WebsiteHeaderRepository;
import Layer.NewStudentManagement.Service.WebsiteHeaderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WebsiteHeaderServiceImpl implements WebsiteHeaderService {

    private final WebsiteHeaderRepository repository;

    // Constructor injection is generally preferred over @Autowired on the field
    public WebsiteHeaderServiceImpl(WebsiteHeaderRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<StudentWebsiteHeaders> getMenuTree() {
        return repository.findByParentIsNullOrderBySortOrderAsc();
    }

    @Override
    public List<StudentWebsiteHeaders> getAllLinks() {
        return repository.findAll();
    }

    @Override
    public Optional<StudentWebsiteHeaders> getLinkById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public StudentWebsiteHeaders createLink(StudentWebsiteHeaders newLink) {
        // Business Logic: Resolve the parent entity if an ID was passed in
        if (newLink.getParent() != null && newLink.getParent().getId() != null) {
            StudentWebsiteHeaders parent = repository.findById(newLink.getParent().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent with ID " + newLink.getParent().getId() + " not found"));
            newLink.setParent(parent);
        }
        return repository.save(newLink);
    }

    @Override
    @Transactional
    public Optional<StudentWebsiteHeaders> updateLink(Long id, StudentWebsiteHeaders updatedData) {
        return repository.findById(id).map(existingLink -> {

            existingLink.setTitle(updatedData.getTitle());
            existingLink.setUrl(updatedData.getUrl());
            existingLink.setSortOrder(updatedData.getSortOrder());
            existingLink.setIsActive(updatedData.getIsActive());
            existingLink.setTarget(updatedData.getTarget());

            // Business Logic: Handle parent reassignment
            if (updatedData.getParent() != null && updatedData.getParent().getId() != null) {
                StudentWebsiteHeaders newParent = repository.findById(updatedData.getParent().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Parent with ID " + updatedData.getParent().getId() + " not found"));
                existingLink.setParent(newParent);
            } else {
                existingLink.setParent(null); // Make it a root node if parent is null
            }

            return repository.save(existingLink);
        });
    }

    @Override
    @Transactional
    public boolean deleteLink(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
