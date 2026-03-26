package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentPageResponseDTO
{
    private Page<StudentResponseDTO> students;

    private long total;
    private long approved;
    private long rejected;
    private long pending;
}
