package Layer.NewStudentManagement.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
//@Table(name = "payment_transactions")
public class PaymentTransactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fees_collect_id", nullable = false)
    private StudentFeesCollect feesCollect;

    @Column(name = "razorpay_order_id", nullable = false, unique = true, length = 100)
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id", length = 100)
    private String razorpayPaymentId;

    @Column(name = "receipt_no", length = 100)
    private String receiptNo;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.CREATED;

    @Column(name = "error_reason")
    private String errorReason;

    @CreationTimestamp
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_by_role", updatable = false)
    private String createdByRole;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(name = "updated_by")
    private String updatedBy;

    @CreationTimestamp
    @Column(name = "updated_by_role")
    private String updatedByRole;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum TransactionStatus {
        CREATED, SUCCESS, FAILED
    }

//    // --- GETTERS AND SETTERS ---
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//    public StudentFees getFee() { return fee; }
//    public void setFee(StudentFees fee) { this.fee = fee; }
//    public String getRazorpayOrderId() { return razorpayOrderId; }
//    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }
//    public String getRazorpayPaymentId() { return razorpayPaymentId; }
//    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }
//    public BigDecimal getAmount() { return amount; }
//    public void setAmount(BigDecimal amount) { this.amount = amount; }
//    public TransactionStatus getStatus() { return status; }
//    public void setStatus(TransactionStatus status) { this.status = status; }
//    public String getErrorReason() { return errorReason; }
//    public void setErrorReason(String errorReason) { this.errorReason = errorReason; }
//    public LocalDateTime getCreatedAt() { return createdAt; }
//    public LocalDateTime getUpdatedAt() { return updatedAt; }
}