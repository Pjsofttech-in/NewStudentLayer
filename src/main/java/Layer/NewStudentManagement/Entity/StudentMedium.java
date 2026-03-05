package Layer.NewStudentManagement.Entity;

import Layer.NewStudentManagement.Enum.Role;
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
public class StudentMedium
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mid;
    private String mediumName;
    @Email
    private String createdByEmail;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String branchCode;

    @OneToMany(mappedBy = "medium", cascade = CascadeType.ALL)
    private List<StudentClassRoom> classRooms;

    @OneToMany(mappedBy = "medium", cascade = CascadeType.ALL)
    private List<StudentEntity> students;

    @OneToMany(mappedBy = "medium", cascade = CascadeType.ALL)
    private List<StudentStandardFees> standardFeesList;


}
