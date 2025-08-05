package Layer.NewStudentManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GenderCountResponse
{
    private Long maleCount;
    private Long femaleCount;
    private Long otherCount;

    public GenderCountResponse(Long maleCount, Long femaleCount, Long otherCount) {
        this.maleCount = maleCount;
        this.femaleCount = femaleCount;
        this.otherCount = otherCount;
    }
}
