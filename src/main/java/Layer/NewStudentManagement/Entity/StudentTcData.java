package Layer.NewStudentManagement.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentTcData
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate tcDate;
    @Column(length = 12)
    private String tcNumber;
    private String studentEmail;

    private String role;
    private String createdByEmail;
    private String branchCode;
    private boolean duplicateTc = false;
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;


}
