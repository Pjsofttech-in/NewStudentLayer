package Layer.NewStudentManagement.Entity;

import Layer.NewStudentManagement.Enum.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentSports
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String height;
    private String weight;
    @Enumerated(EnumType.STRING)
    private Role role;
    private Boolean sportYesNo;
    private String sportsName;
    private Boolean sportParticipation;
    private String noOfYearsPlayed;
    private String levelOfParticipation;
    private String sportsInjuries;
    private String achievement;
    private String internationaldetail;

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

}
