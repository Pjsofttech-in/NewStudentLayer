package Layer.NewStudentManagement.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "student_device",
        indexes = {
                @Index(name = "idx_student_device_student_id", columnList = "student_id"),
                @Index(name = "idx_student_device_fid", columnList = "fid")
        }
)
public class StudentDeviceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_student_device_student")
    )
    private StudentEntity student;

    @Column(
            name = "firebase_installation_id",
            nullable = false,
            unique = true,
            length = 255
    )
    private String firebaseInstallationId;

    @Column(name = "device_type", length = 30)
    private String deviceType;

    @Column(name = "device_name", length = 100)
    private String deviceName;

    @Column(name = "app_version", length = 50)
    private String appVersion;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        lastSeenAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}