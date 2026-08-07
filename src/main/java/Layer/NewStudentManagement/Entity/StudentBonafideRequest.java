package Layer.NewStudentManagement.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentBonafideRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link back to the student who requested it
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

    @Column(nullable = false)
    private String reason; // e.g., "Bank Loan", "Passport Renewal", "Bus Pass"

    @Column(nullable = false)
    private LocalDate requestDate = LocalDate.now();

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    private String remarks; // Staff remarks (especially useful if REJECTED)

    private String processedByEmail; // Which staff member approved/rejected it
    private String processedByRole;
    private LocalDate processedDate;

    private String createdByEmail; // Which staff member approved/rejected it
    private String createdByRole;
    @CreationTimestamp
    private LocalDate createdByDate;

    private String updatedByEmail; // Which staff member approved/rejected it
    private String updatedByRole;

    @UpdateTimestamp
    private LocalDate updatedByDate;


    private String branchCode;
}