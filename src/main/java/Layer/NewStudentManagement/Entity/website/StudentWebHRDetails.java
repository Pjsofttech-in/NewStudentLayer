package Layer.NewStudentManagement.Entity.website;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentWebHRDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hrName;
    private String email;
    private String contact;
    private String url;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

    @OneToMany(mappedBy = "webHRDetails",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StudentWebJobCareerOption> webJobCareerOptions;

    @ManyToOne
    @JoinColumn(name = "security_url_id")
    @JsonIgnore
    private StudentWebSecurityUrl webSecurityUrl;
}
