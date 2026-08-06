package Layer.NewStudentManagement.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentMiscFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link back to the academic context (Year, Standard, etc.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_fees_id", nullable = false)
    private StudentFees studentFees;

    // What kind of fee is this? (e.g., "Library Fine", "Gathering Fee")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fee_component_id", nullable = false)
    private StudentFeeComponentsMaster feeComponent;

    @Column(nullable = false)
    private Double amount;
    
    private Double paidAmount = 0.0;
    private Double pendingAmount = 0.0;

    private LocalDate assignedDate = LocalDate.now();

    private LocalDate dueDate;
    
    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, PARTIAL, PAID, CANCELLED
    
    private String description; // Optional note

    private String branchCode; // Optional note

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @Column(name = "created_by_role", updatable = false)
    private String createdByRole;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_by_role")
    private String updatedByRole;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum FeesStatus {
        PAID, PENDING, PARTIALLY_PAID
    }
}