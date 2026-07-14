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
public class StudentWebTopper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long topperId;

    private String name;
    private String totalMarks;
    private String post;

    @Column(name = "`rank`")
    private String rank;

    private Integer year;
    private String topperImage;
    private String topperColor;
    private String url;

    // List of topper image URLs
    @ElementCollection
    @CollectionTable(name = "topper_images", joinColumns = @JoinColumn(name = "topper_id"))
    @Column(name = "image_url")
    private List<String> topperImages;

    // List of topper image IDs
    @ElementCollection
    @CollectionTable(name = "topper_image_ids", joinColumns = @JoinColumn(name = "topper_id"))
    @Column(name = "image_url_id")
    private List<Integer> imageUrlIds;

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

    @ManyToOne
    @JoinColumn(name = "security_url_id")
    @JsonIgnore
    private StudentWebSecurityUrl webSecurityUrl;
}
