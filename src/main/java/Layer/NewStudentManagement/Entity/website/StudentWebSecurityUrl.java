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
public class StudentWebSecurityUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String url;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

    @OneToMany(mappedBy = "webSecurityUrl", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StudentWebManuBar> webManuBars;

    @OneToMany(mappedBy = "webSecurityUrl", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StudentWebAboutUs> webAboutUses;

    @OneToMany(mappedBy = "webSecurityUrl", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StudentWebAwardsAndAccolades> webAwardsAndAccolades;

    @OneToMany(mappedBy = "webSecurityUrl" , cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StudentWebCourse> webCourse;

    @OneToMany(mappedBy = "webSecurityUrl", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StudentWebFaculty> webFaculty;

    @OneToMany(mappedBy = "webSecurityUrl", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StudentWebFooter> webFooter;

    @OneToMany(mappedBy = "webSecurityUrl", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StudentWebContactForm> webContactForms;

    @OneToMany(mappedBy = "webSecurityUrl", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StudentWebFacultyTitle> webFacultyTitles;

    @OneToMany(mappedBy = "webSecurityUrl", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StudentWebCounter> webCounters;

    @OneToMany(mappedBy = "webSecurityUrl", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StudentWebMapAndImages> webMapAndImages;

    @OneToMany(mappedBy = "webSecurityUrl", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StudentWebTestimonials> testimonials;

    @OneToMany(mappedBy = "webSecurityUrl", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StudentWebVisionMission> webVisionMissions;

    @OneToMany(mappedBy = "webSecurityUrl", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StudentWebJobCareerOption> webJobCareerOptions;

    @OneToMany(mappedBy = "webSecurityUrl", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StudentWebHRDetails> webHRDetails;

    @OneToMany(mappedBy = "webSecurityUrl", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StudentWebGallery> galleries;

    @OneToMany(mappedBy = "webSecurityUrl", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StudentWebSlideBar> webSlideBars;

    @OneToMany(mappedBy = "webSecurityUrl", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StudentWebTopper> webToppers;
}

