package Layer.NewStudentManagement.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Entity
public class StudentNotification
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String noticeName;
    private String noticeDescription;
    private LocalDate createdAt = LocalDate.now();
    private Long classRoomId;

    @Email
    private String createdByEmail;

    private String role;
    private String branchCode;
}
