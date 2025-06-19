package Layer.NewStudentManagement.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentAddressDTO
{
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
    private String fathersContact;
    private String whatsappNumber;
    private boolean sameAsCurrent;
    private String incomeRanges;
}