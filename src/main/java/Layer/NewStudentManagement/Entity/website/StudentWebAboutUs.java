package Layer.NewStudentManagement.Entity.website;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentWebAboutUs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String aboutUsTitle;
    private String aboutUsDescription;
    private String aboutUsImage;
    private String url;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

    @ManyToOne
    @JoinColumn(name = "security_url_id")
    @JsonIgnore
    private StudentWebSecurityUrl webSecurityUrl;
}

