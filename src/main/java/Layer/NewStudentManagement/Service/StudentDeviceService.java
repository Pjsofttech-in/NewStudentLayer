package Layer.NewStudentManagement.Service;

import Layer.NewStudentManagement.DTO.DeviceRegistrationRequest;

public interface StudentDeviceService {

    void registerDevice(DeviceRegistrationRequest request);

    void deactivateDevice(String fid);
}