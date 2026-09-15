package Layer.NewStudentManagement.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceRegistrationRequest {

    @NotBlank(message = "FID is required")
    private String fid;

    private String deviceType;

    private String deviceName;

    private String appVersion;
}