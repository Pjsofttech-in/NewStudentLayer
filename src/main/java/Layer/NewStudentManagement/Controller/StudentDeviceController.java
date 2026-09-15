package Layer.NewStudentManagement.Controller;

import Layer.NewStudentManagement.DTO.DeviceRegistrationRequest;
import Layer.NewStudentManagement.Service.StudentDeviceService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class StudentDeviceController {

    private final StudentDeviceService studentDeviceService;

    @PostMapping("/register")
    public ResponseEntity<?> registerDevice(
            @Valid @RequestBody DeviceRegistrationRequest request) {

        studentDeviceService.registerDevice(request);

        return ResponseEntity.ok(
                "Device registered successfully"
        );
    }

    @DeleteMapping("/deactivate")
    public ResponseEntity<?> deactivateDevice(
            @RequestParam String fid) {

        studentDeviceService.deactivateDevice(fid);

        return ResponseEntity.ok(
                "Device deactivated successfully"
        );
    }
}