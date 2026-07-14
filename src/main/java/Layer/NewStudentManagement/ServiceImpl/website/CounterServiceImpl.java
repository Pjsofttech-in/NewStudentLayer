package Layer.NewStudentManagement.ServiceImpl.website;

import Layer.NewStudentManagement.Entity.website.StudentWebCounter;
import Layer.NewStudentManagement.Entity.website.StudentWebSecurityUrl;
import Layer.NewStudentManagement.Exception.ResourceNotFoundException;
import Layer.NewStudentManagement.Repository.website.CounterRepository;
import Layer.NewStudentManagement.Repository.website.SecurityUrlrepository;
import Layer.NewStudentManagement.Service.website.CounterService;
import Layer.NewStudentManagement.Service.website.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CounterServiceImpl implements CounterService {

    @Autowired
    private CounterRepository repository;

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
        return (url == null) ? "" : url.split(",")[0].trim().toLowerCase();
    }

    @Override
    public StudentWebCounter createCounter(StudentWebCounter webCounter, String role, String email, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create counter");
        }

        String branchCode = permissionService.fetchBranchCode(role, email);
        StudentWebSecurityUrl webSecurityUrl = securityUrlRepository.findByUrl(normalizeUrl(url))
                .orElseThrow(() -> new ResourceNotFoundException("URL not found"));

        webCounter.setRole(role);
        webCounter.setCreatedByEmail(email);
        webCounter.setBranchCode(branchCode);
        webCounter.setWebSecurityUrl(webSecurityUrl);
        webCounter.setUrl(url);

        return repository.save(webCounter);
    }

    @Override
    public List<StudentWebCounter> getAllByBranchCode(String role, String email, String url, String branchCode) {
        validateUrlExists(url,branchCode);

        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view counters");
        }

        return repository.findAllByBranchCode(branchCode);
    }


    @Override
    public StudentWebCounter updateCounter(Long id, StudentWebCounter webCounter, String role, String email, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update counter");
        }

        StudentWebCounter existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Counter not found"));

        // Update values
        existing.setCounterName1(webCounter.getCounterName1());
        existing.setCountValue1(webCounter.getCountValue1());
        existing.setCounterColor1(webCounter.getCounterColor1());

        existing.setCounterName2(webCounter.getCounterName2());
        existing.setCountValue2(webCounter.getCountValue2());
        existing.setCounterColor2(webCounter.getCounterColor2());

        existing.setCounterName3(webCounter.getCounterName3());
        existing.setCountValue3(webCounter.getCountValue3());
        existing.setCounterColor3(webCounter.getCounterColor3());

        existing.setUrl(webCounter.getUrl());

        return repository.save(existing);
    }

    @Override
    public void deleteCounter(Long id, String role, String email, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete counter");
        }

        StudentWebCounter webCounter = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Counter not found"));

        repository.delete(webCounter);
    }

    @Override
    public StudentWebCounter getCounterById(Long id, String role, String email, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view counter");
        }

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Counter not found"));
    }
}