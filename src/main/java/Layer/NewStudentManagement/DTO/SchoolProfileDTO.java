package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SchoolProfileDTO
{
    private Long id;
    private String schoolName;
    private String schoolLogo;
    private String schoolAddress;
    private Long contactNumber;
    private String schoolEmail;
    private String place;
    private String societyName;
    private String indexNumber;
    private String board;
    private String schoolSlug;
    private String schoolUrl;
    private String whatsappLink;
    private String facebookLink;
    private String instagramLink;
    private String twitterLink;

    private List<String> images;
}
