package Layer.NewStudentManagement.Entity.website;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentWebJobCareerOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private String description;
    private String location;
    private String salaryRange;
//    private String JobCareerOptionColor;
    @Column(length = 500)
    private String responsibilities;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate postDate;
    private String resumeUrl;
    private String lastDateToApply;
    private String url;
    private String jobColour;
    private String jobVacancy;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

    @ManyToOne
    @JoinColumn(name = "security_url_id")
    @JsonIgnore
    private StudentWebSecurityUrl webSecurityUrl;

    @ManyToOne
    @JoinColumn(name ="Web_Hr_Details_id")
//    @JsonIgnore
    private StudentWebHRDetails webHRDetails;
}
