package Layer.NewStudentManagement.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentResult
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private StudentEntity student;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private StudentExam exam;

    private Integer totalObtained;
    private Integer totalMax;
    private Double percentage;
    private String overAllStatus;
    @Email
    private String createdByEmail;
    private String role;
    private String branchCode;

    @OneToMany(mappedBy = "studentResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentResultDetail> details = new ArrayList<>();

}
