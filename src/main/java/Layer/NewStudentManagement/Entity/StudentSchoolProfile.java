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
    private Long udiseNumber;
    private String schoolLogo;
    private String schoolAddress;
    private Long contactNumber;
    private String schoolEmail;
    private String place;
    private String societyName;
    private String indexNumber;
    private String board;

    @Email
    private String createdByEmail;
    private String branchCode;

    private String role;
}
