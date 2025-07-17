package Layer.NewStudentManagement.Entity;

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
public class StudentFeeSchedule
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_fees_id")
    private StudentFees studentFees;

    private String feesType;    // "Monthly" or "Installment"
    private String month;   // "July", "August" OR "1st Installment", etc.
    private Double collectAmount;
    private boolean isPaid = false;

}
