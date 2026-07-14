package Layer.NewStudentManagement.ServiceImpl.website;

import Layer.NewStudentManagement.DTO.WebJobCareerOptionDTO;
import Layer.NewStudentManagement.Entity.website.StudentWebHRDetails;
import Layer.NewStudentManagement.Entity.website.StudentWebJobCareerOption;
import Layer.NewStudentManagement.Entity.website.StudentWebSecurityUrl;
import Layer.NewStudentManagement.Exception.ResourceNotFoundException;
import Layer.NewStudentManagement.Repository.website.JobCareerOptionRepository;
import Layer.NewStudentManagement.Repository.website.SecurityUrlrepository;
import Layer.NewStudentManagement.Repository.website.WebHRDetailsRepository;
import Layer.NewStudentManagement.Service.S3Service;
import Layer.NewStudentManagement.Service.website.JobCareerOptionService;
import Layer.NewStudentManagement.Service.website.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class JobCareerOptionServiceImpl implements JobCareerOptionService {

    @Autowired
    private JobCareerOptionRepository repository;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private SecurityUrlrepository securityUrlRepository;

    @Autowired
    private WebHRDetailsRepository webHRDetailsRepository;

    @Autowired
    private S3Service s3Service;


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
    public WebJobCareerOptionDTO create(StudentWebJobCareerOption option, String role, String email, MultipartFile resumeFile, String url, Long webHRDetailsId) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "POST")) {
            throw new AccessDeniedException("No permission to create job post");
        }

        String branchCode = permissionService.fetchBranchCode(role, email);
        StudentWebHRDetails webHRDetails = webHRDetailsRepository.findById(webHRDetailsId)
                .orElseThrow(() -> new ResourceNotFoundException("HR not found by id: " + webHRDetailsId));

        StudentWebSecurityUrl webSecurityUrl = securityUrlRepository.findByUrl(normalizeUrl(url))
                .orElseThrow(() -> new ResourceNotFoundException("Provided URL does not exist"));

        List<StudentWebJobCareerOption> existingJobs = repository.findAll();
        if (!existingJobs.isEmpty()) {
            option.setJobColour(existingJobs.get(0).getJobColour());
        }

        option.setRole(role);
        option.setCreatedByEmail(email);
        option.setBranchCode(branchCode);
        option.setUrl(url);
        option.setPostDate(LocalDate.now());
        option.setWebHRDetails(webHRDetails);
        option.setWebSecurityUrl(webSecurityUrl);

        if (resumeFile != null && !resumeFile.isEmpty()) {
            try {
                String uploadedUrl = s3Service.uploadImage(resumeFile, branchCode);
                option.setResumeUrl(uploadedUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload resume PDF to S3", e);
            }
        }

        return mapToDTO(repository.save(option));
    }

    @Override
    public WebJobCareerOptionDTO update(Long id, StudentWebJobCareerOption option, String role, String email, MultipartFile resumeFile, String url, Long webHRDetailsId) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "PUT")) {
            throw new AccessDeniedException("No permission to update job post");
        }

        StudentWebJobCareerOption existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job post not found"));

        existing.setTitle(option.getTitle() != null ? option.getTitle() : existing.getTitle());
        existing.setDescription(option.getDescription() != null ? option.getDescription() : existing.getDescription());
        existing.setLocation(option.getLocation() != null ? option.getLocation() : existing.getLocation());
        existing.setSalaryRange(option.getSalaryRange() != null ? option.getSalaryRange() : existing.getSalaryRange());
        existing.setResponsibilities(option.getResponsibilities() != null ? option.getResponsibilities() : existing.getResponsibilities());
        existing.setPostDate(option.getPostDate() != null ? option.getPostDate() : existing.getPostDate());
        existing.setLastDateToApply(option.getLastDateToApply() != null ? option.getLastDateToApply() : existing.getLastDateToApply());
        existing.setUrl(option.getUrl() != null ? option.getUrl() : existing.getUrl());
        existing.setJobVacancy(option.getJobVacancy() != null ? option.getJobVacancy() : existing.getJobVacancy());

        String branchCode = permissionService.fetchBranchCode(role, email);

        if (resumeFile != null && !resumeFile.isEmpty()) {
            try {
                String uploadedUrl = s3Service.uploadImage(resumeFile, branchCode);
                existing.setResumeUrl(uploadedUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload resume to S3", e);
            }
        }


        if (webHRDetailsId != null) {
            StudentWebHRDetails webHRDetails = webHRDetailsRepository.findById(webHRDetailsId)
                    .orElseThrow(() -> new ResourceNotFoundException("HR not found by id: " + webHRDetailsId));
            existing.setWebHRDetails(webHRDetails);
        }

        if (option.getJobColour() != null && !option.getJobColour().equals(existing.getJobColour())) {
            List<StudentWebJobCareerOption> allJobs = repository.findAll();
            for (StudentWebJobCareerOption job : allJobs) {
                job.setJobColour(option.getJobColour());
            }
            repository.saveAll(allJobs);
        }

        return mapToDTO(repository.save(existing));
    }



    @Override
    public List<WebJobCareerOptionDTO> getAllByBranchCode(String role, String email, String url, String branchCode) {
        validateUrlExists(url,branchCode);
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view job posts");
        }

        return repository.findAllByBranchCode(branchCode)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public WebJobCareerOptionDTO getById(Long id, String role, String email, String url) {
        validateUrlExists(url,null);
        if (!permissionService.hasPermission(role, email, "GET")) {
            throw new AccessDeniedException("No permission to view job post");
        }

        StudentWebJobCareerOption job = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job post not found"));

        return mapToDTO(job);
    }

    private WebJobCareerOptionDTO mapToDTO(StudentWebJobCareerOption option) {
        WebJobCareerOptionDTO dto = new WebJobCareerOptionDTO();
        dto.setId(option.getId());
        dto.setTitle(option.getTitle());
        dto.setDescription(option.getDescription());
        dto.setLocation(option.getLocation());
        dto.setSalaryRange(option.getSalaryRange());
        dto.setResponsibilities(option.getResponsibilities());
        dto.setPostDate(option.getPostDate());
        dto.setResumeUrl(option.getResumeUrl());
        dto.setLastDateToApply(option.getLastDateToApply());
        dto.setJobVacancy(option.getJobVacancy());
        dto.setUrl(option.getUrl());
        dto.setJobColour(option.getJobColour());
        dto.setCreatedByEmail(option.getCreatedByEmail());
        dto.setRole(option.getRole());
        dto.setBranchCode(option.getBranchCode());

        if (option.getWebHRDetails() != null) {
            dto.setWebHRDetailsId(option.getWebHRDetails().getId());
//            dto.setWebHRDetailsName(option.getWebHRDetails().getName()); // Replace with actual field
        }

        return dto;
    }

    @Override
    public void delete(Long id, String role, String email, String url) {
        validateUrlExists(url,null);

        if (!permissionService.hasPermission(role, email, "DELETE")) {
            throw new AccessDeniedException("No permission to delete job post");
        }

        StudentWebJobCareerOption job = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job post not found with ID: " + id));

        repository.delete(job);
    }

}