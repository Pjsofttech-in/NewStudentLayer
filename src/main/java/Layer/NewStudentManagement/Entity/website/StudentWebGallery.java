package Layer.NewStudentManagement.Entity.website;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentWebGallery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long galleryId;

    private String eventName;
    private String month;
    private Integer year;
    private String galleryColor;
    private String url;

    // List of gallery image URLs
    @ElementCollection
    @CollectionTable(name = "gallery_images", joinColumns = @JoinColumn(name = "gallery_id"))
    @Column(name = "image_url")
    private List<String> galleryImages = new ArrayList<>();

    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

    @ManyToOne
    @JoinColumn(name = "security_url_id")
    @JsonIgnore
    private StudentWebSecurityUrl webSecurityUrl;
}
