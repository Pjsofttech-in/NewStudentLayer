package Layer.NewStudentManagement.ServiceImpl.website;

import Layer.NewStudentManagement.Entity.website.StudentWebFacultyTitle;
import Layer.NewStudentManagement.Entity.website.StudentWebSecurityUrl;
import Layer.NewStudentManagement.Exception.ResourceNotFoundException;
import Layer.NewStudentManagement.Repository.website.FacultyTitleRepository;
import Layer.NewStudentManagement.Repository.website.SecurityUrlrepository;
import Layer.NewStudentManagement.Service.website.FacultyTitleService;
import Layer.NewStudentManagement.Service.website.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacultyTitleServiceImpl implements FacultyTitleService {

    @Autowired
    private FacultyTitleRepository repository;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private SecurityUrlrepository securityUrlRepository;

    private void validateUrlExists(String url, String branchCode) {

        String normalizedUrl = normalizeUrl(url);
        if (branchCode == null || branchCode.isBlank()) {
            securityUrlRepository.findByUrl(normalizedUrl)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Provided URL [" + url + "] does not exist"
                    ));
            return;
        }

        securityUrlRepository.findByUrlAndBranchCode(normalizedUrl, branchCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "URL [" + url + "] is not allowed for branchCode [" + branchCode + "]"
                ));
    }


    private String normalizeUrl(String url) {
        return url == null ? "" : url.split(",")[0].trim().toLowerCase();
    }

    @Override
    public StudentWebFacultyTitle createFacilityTitle(StudentWebFacultyTitle webFacultyTitle, String role, String email, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create facility title");
        }

        String branchCode = permissionService.fetchBranchCode(role, email);
        StudentWebSecurityUrl webSecurityUrl = securityUrlRepository.findByUrl(normalizeUrl(url))
                .orElseThrow(() -> new ResourceNotFoundException("Provided URL does not exist in security URL table"));

        webFacultyTitle.setRole(role);
        webFacultyTitle.setCreatedByEmail(email);
        webFacultyTitle.setBranchCode(branchCode);
        webFacultyTitle.setWebSecurityUrl(webSecurityUrl);
        webFacultyTitle.setUrl(url);

        return repository.save(webFacultyTitle);
    }

    @Override
    public List<StudentWebFacultyTitle> getAllFacilityTitlesByBranchCode(String role, String email, String url, String branchCode) {
        validateUrlExists(url,branchCode);
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view facility titles by branch");
        }
        return repository.findAllByBranchCode(branchCode);
    }


    @Override
    public StudentWebFacultyTitle updateFacilityTitle(Long id, StudentWebFacultyTitle updated, String role, String email, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update facility title");
        }

        StudentWebFacultyTitle existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FacilityTitle not found"));

        existing.setFacilityTitle(updated.getFacilityTitle() != null ? updated.getFacilityTitle() : existing.getFacilityTitle());

        return repository.save(existing);
    }

    @Override
    public void deleteFacilityTitle(Long id, String role, String email, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete facility title");
        }

        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("FacilityTitle not found"));
        repository.deleteById(id);
    }

    @Override
    public StudentWebFacultyTitle getFacilityTitleById(Long id, String role, String email, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view facility title");
        }

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FacilityTitle not found"));
    }
}