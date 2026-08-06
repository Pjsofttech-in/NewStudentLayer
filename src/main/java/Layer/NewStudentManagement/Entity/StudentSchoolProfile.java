package Layer.NewStudentManagement.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentSchoolProfile
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String schoolName;
    private String schoolLogo;
    private String schoolAddress;
    private Long contactNumber;
    private String schoolEmail;
    private String place;
    private String societyName;
    private String board;
    @Column(unique = true)
    private String schoolSlug;
    private String schoolUrl;
    private String whatsappLink;
    private String facebookLink;
    private String instagramLink;
    private String twitterLink;

    private Long udiseNumber;
    private String indexNumber;
    private String dteCode;
    private String msbteCode;
    private String aisheCode;


    @Email
    private String createdByEmail;
    private String branchCode;
    private String role;


    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentProfileImage> images;
}
