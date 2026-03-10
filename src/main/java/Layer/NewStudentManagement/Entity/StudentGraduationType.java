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
public class StudentGraduationType
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String graduationType;
    @Email
    private String createdByEmail;

    private String role;
    private String branchCode;

    @ManyToOne
    @JoinColumn(name = "stream_id")
    private StudentStream stream;

    @OneToMany(mappedBy = "graduationType", cascade = CascadeType.ALL)
    private List<StudentDegreeName> degreeNames;


}
