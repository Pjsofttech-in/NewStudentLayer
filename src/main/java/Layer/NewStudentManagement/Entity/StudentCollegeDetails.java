package Layer.NewStudentManagement.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentCollegeDetails {

    @Id
    @Column(name = "student_id")
    private Long id;

    @Column(name = "abc_id", length = 50)
    private String abcId;

    @Column(name = "enrollment_number", unique = true, length = 50)
    private String enrollmentNumber;

    @Column(name = "dte_number", length = 50)
    private String dteNumber;

    // 🌟 One-to-One Mapping: Shared Primary Key relation
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Forces Hibernate to use 'student_id' as both PK and FK
    @JoinColumn(name = "student_id")
    private StudentEntity student;
}