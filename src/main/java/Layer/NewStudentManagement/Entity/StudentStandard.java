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
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StudentStandard
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sid;
    private String standardName;
    @Email
    private String createdByEmail;

    private String role;
    private String branchCode;

    @OneToMany(mappedBy = "standard", cascade = CascadeType.ALL)
    private List<StudentClassRoom> classRooms;

    @OneToMany(mappedBy = "standard", cascade = CascadeType.ALL)
    private List<StudentEntity> students;

    @OneToMany(mappedBy = "standard", cascade = CascadeType.ALL)
    private List<StudentStandardFees> standardFeesList;


}
