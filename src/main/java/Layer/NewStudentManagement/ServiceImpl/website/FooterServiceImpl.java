package Layer.NewStudentManagement.ServiceImpl.website;

import Layer.NewStudentManagement.Entity.website.StudentWebFooter;
import Layer.NewStudentManagement.Entity.website.StudentWebSecurityUrl;
import Layer.NewStudentManagement.Exception.AlreadyExistsException;
import Layer.NewStudentManagement.Exception.ResourceNotFoundException;
import Layer.NewStudentManagement.Repository.website.FooterRepository;
import Layer.NewStudentManagement.Repository.website.SecurityUrlrepository;
import Layer.NewStudentManagement.Service.website.FooterService;
import Layer.NewStudentManagement.Service.website.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FooterServiceImpl implements FooterService {

    @Autowired
    private FooterRepository repository;

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
        if (url == null) return "";
        return url.split(",")[0].trim().toLowerCase();
    }
    @Override
    public StudentWebFooter createFooter(StudentWebFooter webFooter, String role, String email, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create footer");
        }

        String branchCode = permissionService.fetchBranchCode(role, email);

        // Check if a footer already exists for this branch
        repository.findFirstByBranchCode(branchCode).ifPresent(existing -> {
            throw new AlreadyExistsException("Footer already exists for this branch");
        });

        String normalizedUrl = normalizeUrl(url);
        StudentWebSecurityUrl webSecurityUrl = securityUrlRepository.findByUrl(normalizedUrl)
                .orElseThrow(() -> new ResourceNotFoundException("Provided URL does not exist in security URL table"));

        webFooter.setRole(role);
        webFooter.setCreatedByEmail(email);
        webFooter.setBranchCode(branchCode);
        webFooter.setUrl(url);
        webFooter.setWebSecurityUrl(webSecurityUrl);

        return repository.save(webFooter);
    }


    @Override
    public List<StudentWebFooter> getAllFootersByBranchCode(String role, String email, String url, String branchCode) {
        validateUrlExists(url,branchCode);

        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view footer by branch");
        }

        return repository.findAllByBranchCode(branchCode);
    }


    @Override
    public StudentWebFooter updateFooter(Long id, StudentWebFooter webFooter, String role, String email, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update footer");
        }

        StudentWebFooter existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Footer not found"));

        existing.setTitle(webFooter.getTitle() != null ? webFooter.getTitle() : existing.getTitle());
        existing.setFooterColor(webFooter.getFooterColor() != null ? webFooter.getFooterColor() : existing.getFooterColor());
        existing.setInstagramLink(webFooter.getInstagramLink() != null ? webFooter.getInstagramLink() : existing.getInstagramLink());
        existing.setFacebookLink(webFooter.getFacebookLink() != null ? webFooter.getFacebookLink() : existing.getFacebookLink());
        existing.setTwitterLink(webFooter.getTwitterLink() != null ? webFooter.getTwitterLink() : existing.getTwitterLink());
        existing.setYoutubeLink(webFooter.getYoutubeLink() != null ? webFooter.getYoutubeLink() : existing.getYoutubeLink());
        existing.setWhatsappLink(webFooter.getWhatsappLink() != null ? webFooter.getWhatsappLink() : existing.getWhatsappLink());
        existing.setEmail(webFooter.getEmail() != null ? webFooter.getEmail() : existing.getEmail());
        existing.setMobileNumber(webFooter.getMobileNumber() != null ? webFooter.getMobileNumber() : existing.getMobileNumber());
        existing.setAddress(webFooter.getAddress() != null ? webFooter.getAddress() : existing.getAddress());
        existing.setUrl(url != null ? url : existing.getUrl());

        return repository.save(existing);
    }

    @Override
    public void deleteFooter(Long id, String role, String email, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete footer");
        }

        repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Footer not found"));

        repository.deleteById(id);
    }

    @Override
    public StudentWebFooter getFooterById(Long id, String role, String email, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view footer");
        }

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Footer not found"));
    }
}
