package Layer.NewStudentManagement.DTO;


import Layer.NewStudentManagement.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentSportsDTO
{
    private Long id;
    private String height;
    private String weight;
    private Role role;
    private Boolean sportYesNo;
    private String sportsName;
    private Boolean sportParticipation;
    private String noOfYearsPlayed;
    private String levelOfParticipation;
    private String sportsInjuries;
    private String achievement;
    private String internationaldetail;
}