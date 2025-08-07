package Layer.NewStudentManagement.DTO;

import Layer.NewStudentManagement.Entity.StudentEntity;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TCDTO
{
    private Long id;
    private LocalDate tcDate;
    private String tcNumber;
    private String studentEmail;
    private String role;
    private String createdByEmail;
    private String branchCode;
    private boolean duplicateTc;
    private Long studentId;
}
