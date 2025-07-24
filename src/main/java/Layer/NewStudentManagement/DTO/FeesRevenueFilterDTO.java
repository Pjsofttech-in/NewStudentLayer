package Layer.NewStudentManagement.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class FeesRevenueFilterDTO {
    private String institutionType;
    private String academicYear;
    private String standardName;
    private String mediumName;
    private String streamName;
    private String graduationTypeName;
}