package Layer.NewStudentManagement.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentAddress
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String permanentAddress;
    private String plandmark;
    private String pDistrict;
    private String pTaluka;
    private String pcountry;
    private String pcity;
    private String pState;
    private String state;
    private int ppincode;
    private String address;
    private String country;
    private String district;
    private String city;
    private String taluka;
    private String landmark;
    private int pincode;
    private String nationality;
    private String motherName;
    private String fatherProfession;
    private String fathersName;
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String fathersContact;
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String whatsappNumber;
    private boolean sameAsCurrent;
    private String incomeRanges;

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;
}
