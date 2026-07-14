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
@AllArgsConstructor
@NoArgsConstructor
public class StudentWebTestimonials {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testimonialId;

    private String testimonialTitle;
    private String testimonialName;
    private String exam;
    private String post;
    private String testimonialImage;
    private String description;
    private String testimonialColor;
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
