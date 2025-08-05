package Layer.NewStudentManagement.ServiceImpl;

import Layer.NewStudentManagement.DTO.InstituteClientWrapperResponse;
import Layer.NewStudentManagement.DTO.InstituteLoginResponse;
import Layer.NewStudentManagement.Entity.StudentEntity;
import Layer.NewStudentManagement.Entity.StudentTeacher;
import Layer.NewStudentManagement.Repository.StudentRepository;
import Layer.NewStudentManagement.Repository.TeacherRepository;
import Layer.NewStudentManagement.Security.LoginRequest;
import Layer.NewStudentManagement.Security.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StaffService
{
    private final WebClient webClient;


    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    public StaffService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<LoginResponse> loginStaff(LoginRequest request) {
        return webClient.post()
                .uri("/stafflogin")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Login Failed: " + error)))
                )
                .bodyToMono(LoginResponse.class);
    }


    public Map<String, Boolean> getPermissionsByEmail(String email) {

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/permissionForStaff")
                        .queryParam("staffEmail", email)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)  // pass it as-is
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Boolean>>() {})
                .block();
    }

    public Map<String, Object> getCrudPermissionForDepartmentByEmail(String email) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/permissionForDepartment")
                        .queryParam("email", email)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)  // Pass token directly
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

    }

    public Map<String, Object> getCrudPermissionForBranchByEmail(String email) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/permissionForBranch")
                        .queryParam("email", email)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)  // Pass token directly
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

    }

    public List<String> getBranchCodesByInstituteEmail(String instituteEmail) {
        List<List<String>> nestedList = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getBranchCodesByinstituteEmail")
                        .queryParam("instituteEmail", instituteEmail)
                        .build())
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<List<String>>() {})
                .collectList()
                .block();
        return nestedList.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public List<InstituteLoginResponse> getInstituteDetailsOnly(String email) {
        InstituteClientWrapperResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getLayerClientByClientEmail")
                        .queryParam("email", email)
                        .build())
                .retrieve()
                .bodyToMono(InstituteClientWrapperResponse.class)
                .block();

        // Return only instituteResponseDTOS
        return response != null ? response.getInstituteResponseDTOS() : Collections.emptyList();
    }



    public boolean hasPermission(String role, String email, String action) {
        if (role == null || action == null) {
            return false;
        }

        switch (role.toUpperCase()) {
            case "USER" -> {
                return "POST".equalsIgnoreCase(action) || "GET".equalsIgnoreCase(action);
            }

            case "STUDENT" -> {
                try {
                    System.out.println("Checking role for Student: " + email + " Action: " + action);
                    boolean exists = studentRepository.existsByEmail(email);
                    return exists && ("POST".equalsIgnoreCase(action) || "GET".equalsIgnoreCase(action));
                } catch (Exception e) {
                    System.err.println("Error checking student permission: " + e.getMessage());
                    return false;
                }
            }

            case "TEACHER" -> {
                try {
                    System.out.println("Checking role for Teacher: " + email + " Action: " + action);
                    boolean exists = teacherRepository.existsByTeacherEmail(email);
                    return exists && ("POST".equalsIgnoreCase(action) || "GET".equalsIgnoreCase(action));
                } catch (Exception e) {
                    System.err.println("Error checking teacher permission: " + e.getMessage());
                    return false;
                }
            }

            case "BRANCH" -> {
                try {
                    Boolean exists = webClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/existBranchbyemail")
                                    .queryParam("email", email)
                                    .build())
                            .retrieve()
                            .bodyToMono(Boolean.class)
                            .block();
                    return Boolean.TRUE.equals(exists);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            case "STAFF" -> {
                Map<String, Boolean> perms = getPermissionsByEmail(email);
                return switch (action.toUpperCase()) {
                    case "GET" -> Boolean.TRUE.equals(perms.get("cansGet"));
                    case "POST" -> Boolean.TRUE.equals(perms.get("cansPost"));
                    case "PUT" -> Boolean.TRUE.equals(perms.get("cansPut"));
                    case "DELETE" -> Boolean.TRUE.equals(perms.get("cansDelete"));
                    default -> false;
                };
            }

            case "DEPARTMENT" -> {
                Map<String, Object> perms = getCrudPermissionForDepartmentByEmail(email);
                return switch (action.toUpperCase()) {
                    case "GET" -> Boolean.TRUE.equals(perms.get("candGet"));
                    case "POST" -> Boolean.TRUE.equals(perms.get("candPost"));
                    case "PUT" -> Boolean.TRUE.equals(perms.get("candPut"));
                    case "DELETE" -> Boolean.TRUE.equals(perms.get("candDelete"));
                    default -> false;
                };
            }

            default -> {
                return false;
            }
        }
    }

    public String fetchBranchCodeByRole(String role, String email) {
        try {
            switch (role.toLowerCase()) {
                case "branch", "department", "staff" -> {
                    String endpoint = switch (role.toLowerCase()) {
                        case "branch" -> "/branch/getbranchcode";
                        case "department" -> "/department/getbranchcode";
                        case "staff" -> "/staff/getbranchcode";
                        default -> throw new IllegalArgumentException("Invalid role: " + role);
                    };

                    return webClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(endpoint)
                                    .queryParam("email", email)
                                    .build())
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();
                }

                case "teacher" -> {
                    return teacherRepository.findByTeacherEmail(email)
                            .map(StudentTeacher::getBranchCode)
                            .orElseThrow(() -> new RuntimeException("Teacher not found with email: " + email));
                }

                case "student" -> {
                    return studentRepository.findByEmail(email)
                            .map(StudentEntity::getBranchCode)
                            .orElseThrow(() -> new RuntimeException("Student not found with email: " + email));
                }

                default -> throw new IllegalArgumentException("Invalid role: " + role);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch branch code for role: " + role + ", email: " + email, e);
        }
    }


    public Mono<String> getInstituteEmailByBranchCode(String branchCode) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/instituteEmailByBranchCode")
                        .queryParam("branchCode", branchCode)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }


}
