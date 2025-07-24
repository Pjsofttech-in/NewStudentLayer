package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentFeeScheduleDTO
{
    private Long id;
    private String month;
    private boolean paid;
    private LocalDate dueDate;
    private Double amount;
    private String FeesType;
}
