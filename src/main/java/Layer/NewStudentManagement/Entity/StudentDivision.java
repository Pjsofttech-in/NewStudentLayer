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
public class StudentDivision
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long did;
    private String division;
    @Email
    private String createdByEmail;

    private String role;
    private String branchCode;

    @OneToMany(mappedBy = "division", cascade = CascadeType.ALL)
    private List<StudentClassRoom> classRooms;

}
